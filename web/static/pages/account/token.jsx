import React from '/modules/react'
import { useUser, redirectToLoginPage } from '/hooks/account.jsx'

import style from './style.module.less'

const { useState, useEffect } = React

function isoToLocal(iso) {
    if (!iso) {
        return ''
    }
    return new Date(Date.parse(iso)).toLocaleString()
}

async function fetchTokenList(token) {
    const response = await fetch('/api/token/list', {
        headers: { 'X-Token': token }
    })
    const result = await response.json()
    if (result.error) {
        alert(result.error)
    }
    return result.payload || []
}

async function fetchDeleteToken(token, idList) {
    const body = new URLSearchParams()
    idList.forEach(id => body.append('id', id))
    const response = await fetch('/api/token/delete', {
        method: 'POST',
        headers: { 'X-Token': token },
        body,
    })
    const result = await response.json()
    if (result.error) {
        alert(result.error)
    }
    return result.payload
}

function Token(props) {
    const { loading, user, token, redirectToLoginPageIfNotLogin } = useUser()
    const [tokenList, setTokenList] = useState(null)

    useEffect(() => {
        if (!token) {
            return
        }
        ;(async () => {
            const fetched = await fetchTokenList(token)
            setTokenList(fetched || [])
        })()
    }, [user])

    if (loading) {
        return <div>Loading...</div>
    }

    if (redirectToLoginPageIfNotLogin()) {
        return <div>Redirecting to login page...</div>
    }

    if (!tokenList) {
        return (
            <div>
                <span>hi, {user['firstname']}</span>
                <div>Loading token...</div>
            </div>
        )
    }

    async function handleDeleteToken(target, ev) {
        ev.preventDefault()
        if (!confirm('The following token will be permanently deleted, are you sure you want to continue?')) {
            return
        }
        await fetchDeleteToken(token, [target.id])
        if (target.current) {
            redirectToLoginPage()
            return
        }
        const fetched = await fetchTokenList(token)
        setTokenList(fetched)
    }

    return (
        <div>
            <span>hi, {user['firstname']}</span>
            <table className={style['t-table']}>
                <thead>
                <tr>
                    <th/>
                    <th> NAME</th>
                    <th> PRIVILEGE</th>
                    <th> LAST ACTIVE</th>
                    <th> OPERATION</th>
                </tr>
                </thead>
                <tbody>
                {tokenList.map(it => (
                    <tr key={it['id']}>
                        <td>{it['current'] && '*'}</td>
                        <td>{it['name']}</td>
                        <td>{JSON.parse(it['privilege']).join(', ')}</td>
                        <td>{isoToLocal(it['lastActiveTime'])}</td>
                        <td className={style['operation']}>
                            <a href="" onClick={(ev) => handleDeleteToken(it, ev)}>DELETE</a>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    )
}

export default Token
