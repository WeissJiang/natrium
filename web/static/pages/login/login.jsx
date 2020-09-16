import { React } from '/deps.mjs'
import { getLocalItem } from '/modules/storage.mjs'
import { useUser } from '/hooks/user.jsx'

const { useState } = React

function LoginForm(props) {

    if (props.verificating) {
        return (
            <div>
                <span>
                    Verification code: <strong>{props.verificationCode}</strong>, please send the code to
                    <a href="https://t.me/nano_telegram_bot" target="_blank"> nano. </a>
                </span>
            </div>
        )
    } else {
        return (
            <div>
                <form onSubmit={props.handleSubmit}>
                    <span>Please input Telegram name: </span>
                    <input type="text" name="username"/>
                    <button type="submit">下一步</button>
                </form>
            </div>
        )
    }


}

function Login(props) {
    const [token, setToken] = useState(getLocalItem('token'))
    const [loading, user] = useUser(token)
    const [verificating, setVerificating] = useState(false)
    const [verificationCode, setVerificationCode] = useState('')

    if (loading) {
        return <div>loading...</div>
    }
    // 用户已登录
    if (user) {
        return <div>hi, {user.firstname}</div>
    }

    async function createTokenAndWaitVerificating(username) {
        console.log('username', username)
        setVerificating(true)
        setVerificationCode('314159')
    }

    async function handleLoginFromSubmit(ev) {
        ev.preventDefault()
        const username = ev.target.username.value
        await createTokenAndWaitVerificating(username)
    }

    // 用户未登录，token不存在或无效
    return <LoginForm verificating={verificating}
                      verificationCode={verificationCode}
                      handleSubmit={handleLoginFromSubmit}/>
}

export default Login
