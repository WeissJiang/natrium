import React, { useEffect, useState } from 'react'
import styled from 'styled-components'

import { getChatList, getUserList } from '../../apis/user.js'
import { setWebhook } from '../../apis/webhook.js'
import useUser, { redirectToLoginPage } from '../../hooks/useUser.js'
import Loading from '../../components/Loading.jsx'
import Layout from '../../components/Layout.jsx'
import { logout } from '../../apis/token.js'
import Button from '../../components/Button.js'
import UserList from './UserList.jsx'
import ChatList from './ChatList.jsx'

function printJson(o) {
    return JSON.stringify(o, null, 2)
}

const ContentContainer = styled.div`
  max-width: 800px;
  margin: 0 auto;
`

const SetWebhookContainer = styled.div`
  box-sizing: border-box;
  padding: 1rem;
  border-top: 1px solid #000;
`

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
            <ContentContainer>
                <UserList list={userList} />
                <ChatList list={chatList} />
                <SetWebhookContainer>
                    <Button onClick={handleSetWebhook}>设置Webhook</Button>
                    <pre>{printJson(setWebhookResult)}</pre>
                </SetWebhookContainer>
            </ContentContainer>
        </Layout>
    )
}


