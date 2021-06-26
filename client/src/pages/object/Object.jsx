import React, { useEffect, useState } from 'react'
import styled from 'styled-components'
import useUser, { redirectToLoginPage } from '../../hooks/useUser.js'
import Loading from '../../components/Loading.jsx'
import { logout } from '../../apis/token.js'
import Layout from '../../components/Layout.jsx'
import { dropObject, getObjectList, putObject } from '../../apis/object.js'
import Table from '../../components/Table.js'
import { formatBytes } from '../../utils/bytes.js'
import { withNanoApi } from '../../apis/env.js'

const ContentContainer = styled.div`
  max-width: 800px;
  margin: 0 auto;
  overflow: auto;
  padding: 1rem;

  & > .head-line {
    position: sticky;
    left: 0;
    margin: .5rem 0;
    display: flex;
    align-items: center;
    justify-content: space-between;

    & > .title {
      font-size: 1.25rem;
      line-height: 1.75rem;
      font-weight: 500;
    }
  }
`

const Anchor = styled.a`
  text-decoration: inherit;
  color: inherit;
  padding: .125rem .25rem;
  cursor: pointer;
  background: none;
  border: none;
  font-size: 1rem;

  &:hover {
    text-decoration: underline;
  }
`

export default function Object() {
    const { loading, user, token, setToken } = useUser()

    const [dataLoading, setDataLoading] = useState(true)
    const [objectList, setObjectList] = useState([])

    async function loadObjectList() {
        try {
            setDataLoading(true)
            const objectList = await getObjectList(token)
            setObjectList(objectList)
        } finally {
            setDataLoading(false)
        }
    }

    useEffect(() => {
        if (loading || !user) {
            return
        }
        (async () => {
            await loadObjectList()
        })()
    }, [loading, user])

    if (loading) {
        return <Loading/>
    }

    if (!user) {
        redirectToLoginPage()
        return <Loading>重定向到登录页...</Loading>
    }

    async function handleLogout() {
        await logout(token)
        setToken(null)
    }

    async function handleClickDelete(ev) {
        const confirmed = confirm('Delete ' + ev)
        if (!confirmed) {
            return
        }
        try {
            setDataLoading(true)
            await dropObject(token, [ev.key])
            await loadObjectList()
        } finally {
            setDataLoading(false)
        }
    }

    async function handleSelectFile(ev) {
        const files = Array.from(ev.target.files)
        if (!files.length) {
            return
        }
        try {
            setDataLoading(true)
            await putObject(token, files)
            await loadObjectList()
        } finally {
            setDataLoading(false)
        }
    }

    return (
        <Layout loading={dataLoading} username={user.firstname} onLogout={handleLogout}>
            <ContentContainer>
                <div className="head-line">
                    <span className="title">Object List</span>
                    <Anchor as="label" href="#" style={{ marginLeft: '.25rem' }}>
                        Add
                        <input onChange={handleSelectFile} multiple type="file" style={{ display: 'none' }}/>
                    </Anchor>
                </div>
                <Table style={{ width: '100%' }}>
                    <thead>
                    <tr>
                        <th>Key</th>
                        <th>Name</th>
                        <th>Type</th>
                        <th>Size</th>
                        <th>Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    {objectList.map(it => (
                        <tr key={it.key}>
                            <td style={{ textAlign: 'center' }}>
                                <span title={it.key}>{it.key.substring(0, 6)}</span>
                            </td>
                            <td style={{ textAlign: 'center' }}>{it.name}</td>
                            <td style={{ textAlign: 'center' }}>{it.type}</td>
                            <td style={{ textAlign: 'center' }}>{formatBytes(it.size)}</td>
                            <td style={{ textAlign: 'center' }}>
                                <Anchor target="_blank" href={withNanoApi(`/api/object/-/${it.key}`)}>
                                    Open
                                </Anchor>
                                <Anchor as="button" onClick={() => handleClickDelete(it)}>
                                    Delete
                                </Anchor>
                            </td>
                        </tr>
                    ))}
                    {!objectList.length && (
                        <tr>
                            <td style={{ textAlign: 'center', padding: '1rem' }} colSpan="5">Empty</td>
                        </tr>
                    )}
                    </tbody>
                </Table>
            </ContentContainer>
        </Layout>
    )
}