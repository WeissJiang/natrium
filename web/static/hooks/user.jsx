import { React } from '/deps.mjs'

const { useState, useEffect } = React

async function fetchUser(token) {
    const options = {
        cache: 'no-cache',
        headers: {
            'X-Token': token
        },
    }
    const response = await fetch('/api/user/user', options)
    const result = await response.json()
    if (result.error) {
        alert(result.error)
    }
    return result.payload
}

export function useUser(token) {
    const [user, setUser] = useState(null)
    const [loading, setLoading] = useState(true)
    useEffect(() => {
        if (!token) {
            setLoading(false)
            setUser(null)
        } else {
            (async () => {
                const user = await fetchUser(token)
                if (user) {
                    setUser(user)
                }
                setLoading(false)
            })()
        }
    }, [token])
    return [loading, user]
}
