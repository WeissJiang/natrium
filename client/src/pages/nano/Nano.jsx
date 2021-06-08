import React, { useEffect, useState } from 'react'
import { getChatList, getUserList } from '../../apis/user.js'
import { setWebhook } from '../../apis/webhook.js'
import useUser, { redirectToLoginPage } from '../../hooks/useUser.js'
import Loading from '../../components/Loading.jsx'
import Layout from '../../components/Layout.jsx'
import { logout } from '../../apis/token.js'
import Button from '../../components/Button.js'
import UserList from './UserList'
import ChatList from './ChatList'

function printJson(o) {
    return JSON.stringify(o, null, 2)
}

export default function Nano() {
    const { loading, user, token, setToken } = useUser()

    const [userList, setUserList] = useState([])
    const [chatList, setChatList] = useState([])
    const [dataLoading, setDataLoading] = useState(true)

    const [setWebhookResult, setSetWebhookResult] = useState({})

    useEffect(() => {
        if (loading || !user) {
            return
        }
        (async () => {
            try {
                const [fetchedUserList, fetchedChatList] = await Promise.all([getUserList(token), getChatList(token)])
                setUserList(fetchedUserList || [])
                setChatList(fetchedChatList || [])
            } finally {
                setDataLoading(false)
            }
        })()
    }, [loading, user])

    if (loading) {
        return <Loading />
    }

    if (!user) {
        redirectToLoginPage()
        return <Loading>重定向到登录页...</Loading>
    }

    async function handleLogout() {
        await logout(token)
        setToken(null)
    }

    async function handleSetWebhook() {
        const result = await setWebhook(token)
        setSetWebhookResult(result || {})
    }

    return (
        <Layout loading={dataLoading} username={user.firstname} onLogout={handleLogout}>
            <UserList list={userList} />
            <ChatList list={chatList} />
            <hr />
            <div style={{ padding: '1rem' }}>
                <Button onClick={handleSetWebhook}>设置Webhook</Button>
                <pre>{printJson(setWebhookResult)}</pre>
            </div>
        </Layout>
    )
}


