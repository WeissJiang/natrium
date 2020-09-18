import { React } from '/deps.mjs'
import { getLocalItem, setLocalItem } from '/modules/storage.mjs'

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

export function useToken() {
    const [token, internalSetToken] = useState(() => getLocalItem('token'))

    function setLocalToken(token) {
        setLocalItem('token', token)
    }

    function setToken(token) {
        setLocalToken(token)
        internalSetToken(token)
    }

    return {
        token,
        setToken,
        setLocalToken,
    }
}

export function useUser(token) {
    const [user, setUser] = useState(null)
    const [loading, setLoading] = useState(true)
    useEffect(() => {
        if (!token) {
            setLoading(false)
            setUser(null)
            return
        }
        ;(async () => {
            const user = await fetchUser(token)
            if (user) {
                setUser(user)
            }
            setLoading(false)
        })()
    }, [token])
    return { loading, user }
}
