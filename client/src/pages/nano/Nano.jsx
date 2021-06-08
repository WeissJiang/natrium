import React, { useState } from 'react'
import { getChatList, getUserList } from '../../apis/user.js'
import { setWebhook } from '../../apis/webhook.js'
import useUser, { redirectToLoginPage } from '../../hooks/useUser.js'
import Loading from '../../components/Loading.jsx'
import Layout from '../../components/Layout.jsx'
import { logout } from '../../apis/token.js'

function printJson(o) {
    return JSON.stringify(o, null, 2)
}

export default function Nano() {
    const { loading, user, token, setToken } = useUser()

    const [userList, setUserList] = useState([])
    const [chatList, setChatList] = useState([])

    const [result, setResult] = useState({})

    if (loading) {
        return <Loading />
    }

    if (!user) {
        redirectToLoginPage()
        return <Loading>重定向到登录页...</Loading>
    }

    async function handleGetUserList() {
        const fetchedUserList = await getUserList(token)
        const fetchedChatList = await getChatList(token)
        setUserList(fetchedUserList || [])
        setChatList(fetchedChatList || [])
    }

    async function handleSetWebhook() {
        const setWebhookResult = await setWebhook(token)
        setResult(setWebhookResult || {})
    }

    async function handleLogout() {
        await logout(token)
        setToken(null)
    }

    return (
        <Layout username={user.firstname} onLogout={handleLogout}>
            <div style={{ padding: '1rem' }}>
                <button onClick={handleGetUserList}>Get List</button>
                <div>User List</div>
                <pre>{printJson(userList)}</pre>
                <div>Chat List</div>
                <pre>{printJson(chatList)}</pre>
            </div>
            <div style={{ padding: '1rem' }}>
                <button onClick={handleSetWebhook}>Set Webhook</button>
                <div>Result</div>
                <pre>{printJson(result)}</pre>
            </div>
        </Layout>
    )
}


