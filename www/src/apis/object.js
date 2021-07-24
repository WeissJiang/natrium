import { withNanoApi } from './env.js'

export async function getObjectList(token) {
    const response = await fetch(withNanoApi('/api/object/list'), {
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

export async function putObject(token, fileList) {
    const formData = new FormData()
    fileList.forEach(it => formData.append('file', it))
    const response = await fetch(withNanoApi('/api/object/put'), {
        method: 'POST',
        body: formData,
        headers: { 'X-Token': token },
    })
    const result = await response.json()
    if (result.error && response.status !== 403) {
        throw new Error(result.error)
    }
    return result.payload
}

export async function dropObject(token, keyList) {
    const response = await fetch(withNanoApi('/api/object/drop'), {
        method: 'POST',
        headers: {
            'X-Token': token,
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(keyList),
    })
    const result = await response.json()
    if (result.error && response.status !== 403) {
        throw new Error(result.error)
    }
    return result.payload
}