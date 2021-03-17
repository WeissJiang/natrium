import React from 'react'
import { getLocalItem, setLocalItem, removeLocalItem } from '@/utils/storage.mjs'

const { useState, useEffect } = React

export function redirectToLoginPage() {
    const loginUrl = '/#/login'
    const backUrl = location.hash
    const searchParams = new URLSearchParams({ backUrl })
    location.href = `${loginUrl}?${searchParams}`
}

export function getBackUrl() {
    const search = new URL(location.href.replace('/#','')).search
    return new URLSearchParams(search).get('backUrl')
}

const TOKEN = 'token'

async function fetchUser(token) {
    const options = {
        cache: 'no-cache',
        headers: {
            'X-Token': token
        },
    }
    const response = await fetch('/api/user/user', options)
    const result = await response.json()
    if (result.error && response.status !== 403) {
        alert(result.error)
    }
    return result.payload
}

function useToken() {
    const [token, setTokenState] = useState(() => getLocalItem(TOKEN))

    function setLocalToken(token) {
        if (token === null) {
            removeLocalItem(TOKEN)
        } else {
            setLocalItem(TOKEN, token)
        }
    }

    function setToken(token) {
        setLocalToken(token)
        setTokenState(token)
    }

    return {
        token,
        setToken,
        setLocalToken,
    }
}

export function useUser() {
    const { token, setToken, setLocalToken } = useToken()
    const [user, setUser] = useState(null)
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        if (!token) {
            setLoading(false)
            setUser(null)
            return
        }
        setLoading(true)
        ;(async () => {
            const user = await fetchUser(token)
            if (user) {
                setUser(user)
            }
            setLoading(false)
        })()
    }, [token])

    function redirectToLoginPageIfNotLogin() {
        if (!loading && !user) {
            redirectToLoginPage()
            return true
        }
        return false
    }

    return {
        loading,
        user,
        token,
        setToken,
        setLocalToken,
        redirectToLoginPageIfNotLogin,
    }
}

