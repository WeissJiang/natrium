import { React } from '/deps.mjs'
import { getLocalItem } from '/modules/storage.mjs'
import { useUser } from '/hooks/user.jsx'

const { useState } = React

const NOT_LOGGED_IN = 'NOT_LOGGED_IN'
const LOGGED_IN = 'LOGGED_IN'
const LOGGING_IN = 'LOGGING_IN'

function LoginBox() {
    return (
        <div>
            Here login
        </div>
    )
}

function LoggingInBox() {
    return (
        <div>
            Here login
        </div>
    )
}

function Login(props) {
    const [loginState, setLoginState] = useState(NOT_LOGGED_IN)

    const token = getLocalItem('token')
    if (!token && loginState === LOGGED_IN) {
        setLoginState(NOT_LOGGED_IN)
    }
    const user = useUser('token')

    console.log('user', user)

    switch (loginState) {
        case NOT_LOGGED_IN:
            return <LoginBox/>
        case LOGGING_IN:
            return <LoggingInBox/>
        case LOGGED_IN:
            return <div>you are logined</div>
    }
}

export default Login
