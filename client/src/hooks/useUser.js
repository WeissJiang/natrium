import { useEffect, useState } from 'react'

import { getUser } from '../apis/user.js'
import { getLocalItem, removeLocalItem, setLocalItem } from '../utils/storage.js'

const LOGIN_URL = new URL('../pages/login/index.html', import.meta.url)

export function redirectToLoginPage() {
    const backUrl = location.href
    const searchParams = new URLSearchParams({ backUrl })
    location.href = `${LOGIN_URL}?${searchParams}`
}

export function getBackUrl() {
    const search = new URL(location.href).search
    return new URLSearchParams(search).get('backUrl')
}

export const TOKEN = 'token'

export default function useUser() {

    const [token, setTokenState] = useState(() => getLocalItem(TOKEN))

    const [user, setUser] = useState(null)
    const [loading, setLoading] = useState(true)

    function setTokenStore(token) {
        if (token === null) {
            removeLocalItem(TOKEN)
        } else {
            setLocalItem(TOKEN, token)
        }
    }

    function setToken(token) {
        setTokenStore(token)
        setTokenState(token)
    }

    useEffect(() => {
        if (!token) {
            setLoading(false)
            setUser(null)
            return
        }
        setLoading(true)
        ;(async () => {
            const user = await getUser(token)
            if (user) {
                setUser(user)
            }
            setLoading(false)
        })()
    }, [token])

    return {
        loading,
        user,
        token,
        setToken,
        setTokenStore,
    }
}