import React, { useRef, useState } from 'react'
import styled from 'styled-components'
import useUser, { redirectToLoginPage } from '../../hooks/useUser.js'
import Loading from '../../components/Loading.jsx'
import { logout } from '../../apis/token.js'
import Layout from '../../components/Layout.jsx'
import Table from '../../components/Table.js'
import { applyQuery } from '../../apis/postgres.js'

const ContentContainer = styled.div`
  max-width: 1000px;
  margin: 0 auto;
  overflow: auto;
  padding: 1rem;

  & div.form-container {
    position: sticky;
    left: 0;
  }

  & table {
    width: 100%;
  }

  & span.text-null {
    color: #bbb;
  }

  & textarea.input-sql {
    width: 100%;
    min-height: 150px;
    outline: none;
    border: 1px solid #000;
    border-radius: 2px;
    resize: vertical;
  }

  & .text-center {
    text-align: center;
  }

  & button.button-query {
    float: right;
    margin: .25rem 0;
  }

  & button {
    background: none;
    border: none;
    font-size: 1rem;
    line-height: 1.5rem;
    color: #000;
    cursor: pointer;

    &:hover {
      background-color: #000;
      color: #fff;
    }

    &:active {
      background-color: rgba(0, 0, 255, 0.58);
      color: #fff;
    }

    &:disabled {
      background-color: #fff;
      color: #000;
      opacity: .75;
    }
  }
`

export default function Postgres() {
    const { loading, user, token, setToken } = useUser()

    const [dataLoading, setDataLoading] = useState(false)
    const [sql, setSql] = useState('')
    const [selectedSql, setSelectedSql] = useState('')
    const [data, setData] = useState([])
    const textAreaRef = useRef()

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

    async function handleClickQuery() {
        let querySql = selectedSql
        if (!querySql) {
            querySql = sql
        }
        if (!querySql) {
            alert('请输入SQL')
            return
        }
        try {
            setDataLoading(true)
            const data = await applyQuery(token, querySql)
            setData(data)
        } catch (err) {
            alert(err.message)
        } finally {
            setDataLoading(false)
            if (textAreaRef.current) {
                textAreaRef.current.focus()
            }
        }
    }

    function handleSqlChange(ev) {
        setSql(ev.target.value)
    }

    function handleSqlSelect(ev) {
        const target = ev.target
        const start = target.selectionStart
        const end = target.selectionEnd
        const val = target.value.substring(start, end)
        setSelectedSql(val)
    }

    const headers = data.length ? Object.keys(data[0]) : []

    return (
        <Layout username={user.firstname} onLogout={handleLogout}>
            <ContentContainer>
                <div className="form-container">
                    <textarea ref={textAreaRef} className="input-sql" value={sql} onSelect={handleSqlSelect}
                              onChange={handleSqlChange}/>
                    <br/>
                    <button className="button-query" onClick={handleClickQuery}>
                        查询
                    </button>
                </div>
                {!dataLoading && !!data.length && (
                    <Table>
                        <thead>
                        <tr>
                            {headers.map(it => (<th key={it}>{it}</th>))}
                        </tr>
                        </thead>
                        <tbody>
                        {data.map((item, index) => (
                            <tr key={index}>
                                {headers.map(it => {
                                    let val = item[it]
                                    if (val && typeof val === 'object') {
                                        val = val.value
                                    }
                                    if (typeof val === 'boolean') {
                                        val = String(val)
                                    }
                                    if (val === null) {
                                        val = (<span className="text-null">null</span>)
                                    }
                                    return (<td key={it} className="text-center">{val}</td>)
                                })}
                            </tr>
                        ))}
                        </tbody>
                    </Table>
                )}
            </ContentContainer>
        </Layout>
    )
}