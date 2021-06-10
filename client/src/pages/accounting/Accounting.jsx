import React, { useEffect, useState } from "react"
import Layout from "../../components/Layout";
import styled from "styled-components";

import useUser, { redirectToLoginPage } from '../../hooks/useUser.js'
import Loading from '../../components/Loading.jsx'
import { logout } from '../../apis/token.js'
import { getMonthData } from '../../apis/accounting.js'

const ContentContainer = styled.div`
  box-sizing: border-box;
  max-width: 850px;
  margin: 0 auto;
  padding: 1rem;
`

const TitleContainer = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;

  & > input {
    border: none;
  }
`

function getData(originalDetail = [], beginningBalance = 0) {
    const detail = []
    let lastBalance = beginningBalance
    let monthlySummary = 0
    for (const it of originalDetail) {
        const date = it.date
        const totalAmount = it.totalAmount
        const serviceFee = Math.trunc(0.061 * it.totalAmount)
        const balanceAmount = totalAmount - serviceFee
        const singleAmount = 49900
        const quantity = it.quantity
        const handOutAmount = singleAmount * quantity
        const balanceAmountTheDay = lastBalance + balanceAmount - handOutAmount

        lastBalance = balanceAmountTheDay
        monthlySummary += totalAmount
        const item = {
            date,
            totalAmount,
            serviceFee,
            balanceAmount,
            singleAmount,
            quantity,
            handOutAmount,
            balanceAmountTheDay,
        }
        detail.push(item)
    }

    return { detail, beginningBalance, monthlySummary }
}

function getMonth() {
    const date = new Date()
    const year = date.getFullYear()
    let month = date.getMonth() + 1
    if (month < 10) {
        month = '0' + month
    }
    return year + '-' + month
}

export default function Accounting(props) {

    const { loading, user, token, setToken } = useUser()

    const [monthData, setMonthData] = useState({})
    const [month, setMonth] = useState(getMonth())
    const [dataLoading, setDataLoading] = useState(true)

    useEffect(() => {
        if (loading || !user) {
            return
        }
        (async () => {
            try {
                setDataLoading(true)
                const monthData = await getMonthData(token, month)
                setMonthData(monthData || [])
            } finally {
                setDataLoading(false)
            }
        })()
    }, [loading, user, month])

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

    function handleSelectMonth(ev) {
        setMonth(ev.target.value)
    }

    const data = getData(monthData.detail, monthData.beginningBalance)

    function getTitle() {
        if (!month) {
            return ''
        }
        const date = new Date(month)
        return `${date.getFullYear()}年${date.getMonth() + 1}月`
    }

    return (
        <Layout loading={dataLoading} username={user.firstname} onLogout={handleLogout}>
            <ContentContainer className="markdown-body">
                <h1>张三计算器</h1>
                <TitleContainer>
                    <h3>{getTitle()}</h3>
                    <input type="month" value={month} onChange={handleSelectMonth} />
                </TitleContainer>
                <table>
                    <thead>
                    <tr>
                        <th>期初结余</th>
                        <th>本月合计</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>{Number(data.beginningBalance).toLocaleString()}</td>
                        <td>{Number(data.monthlySummary).toLocaleString()}</td>
                    </tr>
                    </tbody>
                </table>
                <h4>明细</h4>
                <table>
                    <thead>
                    <tr>
                        <th>日期</th>
                        <th>总金额</th>
                        <th>手续费（6.1%）</th>
                        <th>结余</th>
                        <th>单笔金额</th>
                        <th>笔数</th>
                        <th>已下发</th>
                        <th>当日结余</th>
                    </tr>
                    </thead>
                    <tbody>
                    {data.detail.map(it => {
                        return (
                            <tr key={it.date}>
                                <td>{it.date}</td>
                                <td>{Number(it.totalAmount).toLocaleString()}</td>
                                <td>{Number(it.serviceFee).toLocaleString()}</td>
                                <td>{Number(it.balanceAmount).toLocaleString()}</td>
                                <td>{Number(it.singleAmount).toLocaleString()}</td>
                                <td>{Number(it.quantity).toLocaleString()}</td>
                                <td>{Number(it.handOutAmount).toLocaleString()}</td>
                                <td>{Number(it.balanceAmountTheDay).toLocaleString()}</td>
                            </tr>
                        )
                    })}
                    </tbody>
                </table>

            </ContentContainer>
        </Layout>
    )
}