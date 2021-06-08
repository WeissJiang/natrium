import React, { useEffect, useState } from 'react'
import styled from 'styled-components'

import { deleteToken, getTokenList } from '../../apis/token.js'
import useUser, { redirectToLoginPage } from '../../hooks/useUser.js'
import Loading from '../../components/Loading.jsx'
import Layout from '../../components/Layout.jsx'
import Table from '../../components/Table.js'

function isoToLocal(iso) {
    if (!iso) {
        return ''
    }
    return new Date(Date.parse(iso)).toLocaleString()
}

const ErrorButton = styled.button`
  box-sizing: border-box;
  text-decoration: inherit;
  margin: 0 .25rem;
  padding: .125rem .5rem;
  font-weight: 500;
  font-size: .875rem;
  line-height: 1.25rem;
  border: none;
  border-radius: 2px;
  background-color: transparent;
  cursor: pointer;
  color: #ef4444;

  &:hover {
    color: #fff;
    background-color: #ef4444;
  }
`

export default function Token(props) {
    const { loading, user, token } = useUser()
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
        <Layout username={user.firstname} loading={!tokenList.length}>
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
                    <tr key={it.id}>
                        <td>{it.name} {it.current && '*'}</td>
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


