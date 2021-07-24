import { withNanoApi } from './env.js'

export async function getUser(token) {
    const response = await fetch(withNanoApi('/api/user/user'), {
        headers: {
            'X-Token': token
        },
    })
    const result = await response.json()
    if (result.error && response.status !== 403) {
        throw new Error(result.error)
    }
    return result.payload
}

export async function getUserList(token) {
    const response = await fetch(withNanoApi('/api/user/list'), {
        method: 'GET',
        headers: { 'X-Token': token },
    })
    const result = await response.json()
    if (result.error) {
        throw new Error(result.error)
    }
    return result.payload
}

export async function getChatList(token) {
    const response = await fetch(withNanoApi('/api/telegram/chat/list'), {
        method: 'GET',
        headers: { 'X-Token': token },
    })
    const result = await response.json()
    if (result.error) {
        throw new Error(result.error)
    }
    return result.payload
}

