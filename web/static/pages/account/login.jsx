import { React } from '/deps.mjs'
import { useUser, useToken } from '/hooks/account.jsx'
import { sleep } from '/modules/schedule.mjs'

const { useState } = React

function getBackUrl() {
    return new URLSearchParams(location.search).get('backUrl')
}

function VerificatingBox(props) {
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
                <input type="text" name="username" required/>
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

function Login(props) {

    const [verificating, setVerificating] = useState(false)
    const [verificationCode, setVerificationCode] = useState('')

    const { token, setLocalToken, setToken } = useToken()
    const { loading, user } = useUser(token)

    if (loading) {
        return <div>Loading...</div>
    }

    // --- 用户已登录

    if (user) {
        async function handleLogout() {
            const response = await fetch('/api/token/delete', {
                method: 'POST',
                headers: { 'X-Token': token }
            })
            const result = await response.json()
            if (result.error) {
                alert(result.error)
                throw new Error(result.error)
            }
            setToken(null)
        }

        return <LoggedInBox user={user} handleLogout={handleLogout}/>
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
            const { verificating } = result.payload
            switch (verificating) {
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

    async function createTokenAndWaitVerificating(username) {
        const response = await fetch('/api/token/createVerificatingToken', {
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
        setVerificating(true)
        setVerificationCode(verificationCode)
        try {
            await pollingTokenVerification(token)
            // 登录成功，处理回调
            const backUrl = getBackUrl()
            if (backUrl) {
                location.href = backUrl
            } else {
                setVerificating(false)
                setVerificationCode('')
                setToken(token)
            }
        } catch (err) {
            if (err.message === 'timeout') {
                alert('Verification timeout')
                setVerificating(false)
                setVerificationCode('')
            }
        }
    }

    async function handleLoginFromSubmit(ev) {
        ev.preventDefault()
        const username = ev.target.username.value
        await createTokenAndWaitVerificating(username)
    }

    if (verificating) {
        return <VerificatingBox verificationCode={verificationCode}/>
    } else {
        return <LoginForm handleSubmit={handleLoginFromSubmit}/>
    }
}

export default Login