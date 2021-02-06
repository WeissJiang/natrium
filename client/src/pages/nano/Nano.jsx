import React from 'react'
import { useUser } from '@/hooks/account.jsx'

import '@/styles/tailwind.css'

const { useState } = React

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
        <div className="max-w-full p-4">
            <div>
                <button className="px-2 py-1 border rounded bg-gray-300 hover:bg-white" onClick={getList}>Get list</button>
                <div>User list</div>
                <pre>{printJson(userList)}</pre>
                <div>Chat list</div>
                <pre>{printJson(chatList)}</pre>
            </div>
            <div>
                <button className="px-2 py-1 border rounded bg-gray-300 hover:bg-white" onClick={setWebhook}>Set Webhook</button>
                <div>Result</div>
                <pre>{printJson(result)}</pre>
            </div>
        </div>
    );
}


