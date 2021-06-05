import React, { useState } from 'react'
import { useUser } from '../../hooks/account.jsx'

async function getUserList(token) {
    const response = await fetch('/api/user/list', {
        method: 'GET',
        headers: { 'X-Token': token },
    })
    const result = await response.json()
    if (result.error) {
        alert(result.error)
        throw new Error(result.error)
    }
    return result.payload
}

async function getChatList(token) {
    const response = await fetch('/api/telegram/chat/list', {
        method: 'GET',
        headers: { 'X-Token': token },
    })
    const result = await response.json()
    if (result.error) {
        alert(result.error)
        throw new Error(result.error)
    }
    return result.payload
}

async function fetchSetWebhook(token) {
    const response = await fetch('/api/telegram/setWebhook', {
        method: 'POST',
        headers: { 'X-Token': token },
    })
    const result = await response.json()
    if (result.error) {
        alert(result.error)
        throw new Error(result.error)
    }
    return result.payload
}

function printJson(o) {
    return JSON.stringify(o, null, 2)
}

export default function Nano() {
    const { loading, token, redirectToLoginPageIfNotLogin } = useUser()

    const [userList, setUserList] = useState([])
    const [chatList, setChatList] = useState([])

    const [result, setResult] = useState({})

    if (loading) {
        return <div>Loading...</div>
    }

    if (redirectToLoginPageIfNotLogin()) {
        return <div>Redirecting to login page...</div>
    }

    async function getList() {
        const fetchedUserList = await getUserList(token)
        const fetchedChatList = await getChatList(token)
        setUserList(fetchedUserList || [])
        setChatList(fetchedChatList || [])
    }

    async function setWebhook() {
        const setWebhookResult = await fetchSetWebhook(token)
        setResult(setWebhookResult || {})
    }

    return (
        <div>
            <div>
                <button onClick={getList}>Get list</button>
                <div>User list</div>
                <pre>{printJson(userList)}</pre>
                <div>Chat list</div>
                <pre>{printJson(chatList)}</pre>
            </div>
            <div>
                <button onClick={setWebhook}>Set Webhook</button>
                <div>Result</div>
                <pre>{printJson(result)}</pre>
            </div>
        </div>
    );
}


