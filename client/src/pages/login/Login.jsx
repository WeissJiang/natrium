import React, { useState } from 'react'
import { sleep } from '../../utils/schedule.js'
import { createVerifyingToken, getTokenVerification, logout } from '../../apis/token.js'
import useUser, { getBackUrl } from '../../hooks/useUser.js'
import LoginFormPage from './LoginFormPage.jsx'
import LoggedInPage from './LoggedInPage.jsx'
import VerifyingPage from './VerifyingPage.jsx'
import Loading from '../../components/Loading.jsx'

async function pollingTokenVerification(token) {
    // 等5秒
    await sleep(5000)
    while (true) {
        const payload = await getTokenVerification(token)
        const { verifying } = payload
        switch (verifying) {
            case 'done': {
                return
            }
            case 'pending': {
                // noop
                break
            }
            case 'timeout': {
                throw new Error('timeout')
            }
        }
        await sleep(2000)
    }
}

export default function Login(props) {

    const [verifying, setVerifying] = useState(false)
    const [verificationCode, setVerificationCode] = useState('')

    const { loading, user, token, setTokenStore, setToken } = useUser()

    if (loading) {
        return <Loading />
    }

    // --- 用户已登录

    if (user) {
        const backUrl = getBackUrl()
        if (backUrl) {
            location.href = backUrl
        }

        async function handleLogout() {
            await logout(token)
            setToken(null)
        }

        return <LoggedInPage user={user} handleLogout={handleLogout} />
    }

    // --- 用户未登录、Token不存在或无效
    async function createTokenAndWaitVerifying(username) {
        const payload = await createVerifyingToken(username)
        const { token, verificationCode } = payload
        setTokenStore(token)
        setVerifying(true)
        setVerificationCode(verificationCode)
        try {
            await pollingTokenVerification(token)
            // 登录成功，处理回调
            const backUrl = getBackUrl()
            if (backUrl) {
                location.href = backUrl
            } else {
                setVerifying(false)
                setVerificationCode('')
                setToken(token)
            }
        } catch (err) {
            if (err.message === 'timeout') {
                alert('Verification timeout')
                setVerifying(false)
                setVerificationCode('')
            }
        }
    }

    async function handleLoginFromSubmit(ev) {
        ev.preventDefault()
        const username = ev.target.username.value
        await createTokenAndWaitVerifying(username)
    }

    if (verifying) {
        return <VerifyingPage verificationCode={verificationCode} />
    } else {
        return <LoginFormPage handleSubmit={handleLoginFromSubmit} />
    }
}


