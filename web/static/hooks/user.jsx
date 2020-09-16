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
    return await response.json()
}

export function useUser(token) {
    const [user, setUser] = useState(null)
    if (!token) {
        return user
    }

    useEffect(() => {
        (async () => {
            const user = await fetchUser(token)
            console.log('useUser', user)
            if (user) {
                setUser(user)
            }
        })()
    }, [token])

    return user
}
