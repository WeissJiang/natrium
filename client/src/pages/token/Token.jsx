import React, { useEffect, useState } from 'react'

import { deleteToken, getTokenList, logout } from '../../apis/token.js'
import useUser, { redirectToLoginPage } from '../../hooks/useUser.js'
import Loading from '../../components/Loading.jsx'
import Layout from '../../components/Layout.jsx'
import Table from './Table.js'
import ErrorButton from './ErrorButton.js'

function isoToLocal(iso) {
    if (!iso) {
        return ''
    }
    return new Date(Date.parse(iso)).toLocaleString()
}

export default function Token(props) {
    const { loading, user, token, setToken } = useUser()
    const [tokenList, setTokenList] = useState([])

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

    async function handleLogout() {
        await logout(token)
        setToken(null)
    }

    async function handleDeleteToken(target, ev) {
        ev.preventDefault()
        if (!confirm('该Token将被永久删除，您确定要继续吗？')) {
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
        <Layout loading={!tokenList.length} username={user.firstname} onLogout={handleLogout}>
            <Table>
                <thead>
                <tr>
                    <th>名字</th>
                    <th>权限</th>
                    <th>上次活跃</th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody>
                {tokenList.map(it => (
                    <tr key={it.id} className={it.current ? 'mark' : ''}>
                        <td>{it.name}</td>
                        <td>{JSON.parse(it.privilege).join(', ')}</td>
                        <td style={{ textAlign: 'center' }}>{isoToLocal(it.lastActiveTime)}</td>
                        <td style={{ textAlign: 'center' }}>
                            <ErrorButton onClick={(ev) => handleDeleteToken(it, ev)}>
                                删除
                            </ErrorButton>
                        </td>
                    </tr>
                ))}
                </tbody>
            </Table>
        </Layout>
    )
}


