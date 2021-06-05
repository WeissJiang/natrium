import React, { useState } from 'react'
import { useUser, getBackUrl } from '../../hooks/account.jsx'
import { sleep } from '../../utils/schedule.js'

function VerifyingBox(props) {
    return (
        <div>
            <span>
                Verification code: <strong>{props.verificationCode}</strong>, please send the code to
                <a href="https://t.me/nano_telegram_bot" target="_blank"> nano. </a>
            </span>
        </div>
    )
}

function LoginForm(props) {
    return (
        <div>
            <form onSubmit={props.handleSubmit}>
                <span>Please input Telegram name: </span>
                <input type="text" name="username" required />
                <button type="submit">Login</button>
            </form>
        </div>
    )
}

function LoggedInBox(props) {
    const { user, handleLogout } = props
    return (
        <div>
            <span>hi, {user['firstname']}</span>
            <button onClick={handleLogout}>Logout</button>
        </div>
    )
}

export default function Login(props) {

    const [verifying, setVerifying] = useState(false)
    const [verificationCode, setVerificationCode] = useState('')

    const { loading, user, token, setLocalToken, setToken } = useUser()

    if (loading) {
        return <div>Loading...</div>
    }

    // --- 用户已登录

    if (user) {
        async function handleLogout() {
            try {
                const response = await fetch('/api/token/deleteSelf', {
                    method: 'POST',
                    headers: { 'X-Token': token }
                })
                const result = await response.json()
                if (result.error) {
                    alert(result.error)
                    console.error(result.error)
                }else {
                    setToken(null)
                }
            } catch (error) {
                console.error(error.message)
            }
        }

        const backUrl = getBackUrl()
        if (backUrl) {
            location.href = backUrl
        }
        return <LoggedInBox user={user} handleLogout={handleLogout} />
    }

    // --- 用户未登录、Token不存在或无效

    async function pollingTokenVerification(token) {
        // 等5秒
        await sleep(5000)
        while (true) {
            const response = await fetch('/api/token/verification', {
                headers: { 'X-Token': token }
            })
            const result = await response.json()
            if (result.error) {
                alert(result.error)
                throw new Error(result.error)
            }
            const { verifying } = result.payload
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

    async function createTokenAndWaitVerifying(username) {
        const response = await fetch('/api/token/createVerifyingToken', {
            method: 'POST',
            body: new URLSearchParams({ username })
        })
        const result = await response.json()
        if (result.error) {
            alert(result.error)
            throw new Error(result.error)
        }
        const { token, verificationCode } = result.payload
        setLocalToken(token)
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
        return <VerifyingBox verificationCode={verificationCode} />
    } else {
        return <LoginForm handleSubmit={handleLoginFromSubmit} />
    }
}


