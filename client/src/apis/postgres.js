import { withNanoApi } from './env'

export async function applyQuery(token, sql) {
    const response = await fetch(withNanoApi('/api/nano/postgres/query'), {
        method: 'POST',
        headers: {
            'X-Token': token,
        },
        body: sql,
    })
    const result = await response.json()
    if (result.error) {
        throw new Error(result.error)
    }
    return result.payload
}