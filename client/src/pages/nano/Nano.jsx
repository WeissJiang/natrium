import React, { useState } from 'react'
import { getChatList, getUserList } from '../../apis/user.js'
import { setWebhook } from '../../apis/webhook.js'
import useUser, { redirectToLoginPage } from '../../hooks/useUser.js'

function printJson(o) {
    return JSON.stringify(o, null, 2)
}

export default function Nano() {
    const { loading, user, token } = useUser()

    const [userList, setUserList] = useState([])
    const [chatList, setChatList] = useState([])

    const [result, setResult] = useState({})

    if (loading) {
        return <div>Loading...</div>
    }

    if (!user) {
        redirectToLoginPage()
        return <div>Redirecting to login page...</div>
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

    return (
        <div>
            <div>
                <button onClick={handleGetUserList}>Get list</button>
                <div>User list</div>
                <pre>{printJson(userList)}</pre>
                <div>Chat list</div>
                <pre>{printJson(chatList)}</pre>
            </div>
            <div>
                <button onClick={handleSetWebhook}>Set Webhook</button>
                <div>Result</div>
                <pre>{printJson(result)}</pre>
            </div>
        </div>
    );
}


