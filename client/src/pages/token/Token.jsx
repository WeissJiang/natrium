import React, { useState, useEffect } from 'react'
import { deleteToken, getTokenList } from '../../apis/token.js'
import useUser, { redirectToLoginPage } from '../../hooks/useUser.js'
import Loading from '../../components/Loading.jsx'

function isoToLocal(iso) {
    if (!iso) {
        return ''
    }
    return new Date(Date.parse(iso)).toLocaleString()
}

export default function Token(props) {
    const { loading, user, token } = useUser()
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
        return <Loading />
    }

    if (!user) {
        redirectToLoginPage()
        return <Loading>重定向到登录页...</Loading>
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


