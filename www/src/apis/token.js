import { withNanoApi } from './env.js'

export async function logout(token) {
    const response = await fetch(withNanoApi('/api/token/deleteSelf'), {
        method: 'POST',
        headers: { 'X-Token': token },
    })
    const result = await response.json()
    if (result.error) {
        throw new Error(result.error)
    }
    return result.payload
}

export async function getTokenVerification(token) {
    const response = await fetch(withNanoApi('/api/token/verification'), {
        headers: { 'X-Token': token }
    })
    const result = await response.json()
    if (result.error) {
        throw new Error(result.error)
    }
    return result.payload
}

export async function createVerifyingToken(username) {
    const response = await fetch(withNanoApi('/api/token/createVerifyingToken'), {
        method: 'POST',
        body: new URLSearchParams({ username })
    })
    const result = await response.json()
    if (result.error) {
        throw new Error(result.error)
    }
    return result.payload
}

export async function getTokenList(token) {
    const response = await fetch(withNanoApi('/api/token/list'), {
        headers: { 'X-Token': token }
    })
    const result = await response.json()
    if (result.error) {
        throw new Error(result.error)
    }
    return result.payload || []
}

export async function deleteToken(token, tokenIdList) {
    const body = tokenIdList.reduce((searchParams, id) => {
        searchParams.append('id', id)
        return searchParams
    }, new URLSearchParams())
    const response = await fetch(withNanoApi('/api/token/delete'), {
        method: 'POST',
        headers: { 'X-Token': token },
        body,
    })
    const result = await response.json()
    if (result.error && response.status !== 403) {
        throw new Error(result.error)
    }
    return result.payload
}