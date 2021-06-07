import React, { useState, useEffect } from 'react'
import { useUser, redirectToLoginPage } from '../../hooks/account.jsx'
import { deleteToken, getTokenList } from '../../apis/token.js'

function isoToLocal(iso) {
    if (!iso) {
        return ''
    }
    return new Date(Date.parse(iso)).toLocaleString()
}

export default function Token(props) {
    const { loading, user, token, redirectToLoginPageIfNotLogin } = useUser()
    const [tokenList, setTokenList] = useState(null)

    useEffect(() => {
        if (!token || !user) {
            return
        }
        ;(async () => {
            const fetched = await getTokenList(token)
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
        await deleteToken(token, [target.id])
        if (target.current) {
            redirectToLoginPage()
            return
        }
        const fetched = await getTokenList(token)
        setTokenList(fetched)
    }

    return (
        <div>
            <span>hi, {user['firstname']}</span>
            <table>
                <thead>
                <tr>
                    <th />
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
                        <td>
                            <a href="" onClick={(ev) => handleDeleteToken(it, ev)}>DELETE</a>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    )
}


