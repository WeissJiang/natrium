import React, { useEffect, useState } from "react"
import Layout from "../../components/Layout"
import styled from "styled-components"

import useUser, { redirectToLoginPage } from '../../hooks/useUser.js'
import Loading from '../../components/Loading.jsx'
import { logout } from '../../apis/token.js'
import { createMonthData, getMonthData, updateMonthData } from '../../apis/accounting.js'
import Button from '../../components/Button.js'

const ContentContainer = styled.div`
  box-sizing: border-box;
  max-width: 900px;
  margin: 0 auto;
  padding: 1rem;
`

const TitleContainer = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;

  & > .input-container {
    box-sizing: border-box;
    display: flex;
    align-items: center;
    gap: 1rem;

    & > input {
      border: none;
    }
  }
`

const StickyTable = styled.table`
`

const EditableTableData = styled.td`
  cursor: pointer;

  &:hover {
    background-color: #eee;
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

function zeroPadding(c) {
    c = String(c)
    switch (c.length) {
        case 0:
            return '00'
        case 1:
            return '0' + c
        default:
            return c
    }
}

function getMonthDates(month) {
    const date = new Date(month)
    const _year = date.getFullYear()
    const _month = zeroPadding(date.getMonth() + 1)
    const lastDate = new Date(date.getFullYear(), date.getMonth() + 1, 0).getDate()
    const dates = []
    for (let i = 1; i <= lastDate; i++) {
        const _date = zeroPadding(i)
        dates.push(`${_year}-${_month}-${_date}`)
    }
    return dates
}

export default function Accounting(props) {

    const { loading, user, token, setToken } = useUser()

    const [monthData, setMonthData] = useState()
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
                setMonthData(monthData)
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

    const data = getData(monthData?.detail, monthData?.beginningBalance)

    function getTitle() {
        if (!month) {
            return ''
        }
        const date = new Date(month)
        return `${date.getFullYear()}年${date.getMonth() + 1}月`
    }

    async function handleEditBeginningBalance() {
        let beginningBalance = prompt('修改期初结余', String(data.beginningBalance))
        if (!beginningBalance) {
            return
        }
        beginningBalance = Number(beginningBalance)
        if (beginningBalance === data.beginningBalance) {
            return
        }
        // sync
        const newMonthData = {
            ...monthData,
            beginningBalance,
        }
        setDataLoading(true)
        await updateMonthData(token, newMonthData)
        setDataLoading(false)
        setMonthData(newMonthData)
    }

    async function handleEditTotalAmount(item) {
        let totalAmount = prompt(`修改${item.date}总金额`, String(item.totalAmount))
        if (!totalAmount) {
            return
        }
        totalAmount = Number(totalAmount)
        if (totalAmount === item.totalAmount) {
            return
        }
        // sync
        const newMonthData = {
            ...monthData,
            detail: monthData.detail.map(it => {
                if (it.date === item.date) {
                    return {
                        ...it,
                        totalAmount
                    }
                } else {
                    return it
                }
            })
        }
        setDataLoading(true)
        await updateMonthData(token, newMonthData)
        setDataLoading(false)
        setMonthData(newMonthData)
    }

    async function handleEditQuantity(item) {
        let quantity = prompt(`修改${item.date}笔数`, String(item.quantity))
        if (!quantity) {
            return
        }
        quantity = Number(quantity)
        if (quantity === item.quantity) {
            return
        }
        // sync
        const newMonthData = {
            ...monthData,
            detail: monthData.detail.map(it => {
                if (it.date === item.date) {
                    return {
                        ...it,
                        quantity
                    }
                } else {
                    return it
                }
            })
        }
        setDataLoading(true)
        await updateMonthData(token, newMonthData)
        setDataLoading(false)
        setMonthData(newMonthData)
    }

    async function handleInitMonth() {
        const newMonthData = {
            month,
            beginningBalance: 0,
            detail: getMonthDates(month).map(it => {
                return {
                    date: it,
                    totalAmount: 0,
                    quantity: 0,
                }
            }),
        }
        setDataLoading(true)
        await createMonthData(token, newMonthData)
        setMonthData(newMonthData)
        setDataLoading(false)
    }

    return (
        <>
            <Layout loading={dataLoading} username={user.firstname} onLogout={handleLogout}>
                <ContentContainer className="markdown-body">
                    <h1>张三计算器</h1>
                    <TitleContainer>
                        <h3>{getTitle()}</h3>
                        <div className="input-container">
                            {!monthData && <Button onClick={handleInitMonth}>初始化</Button>}
                            <input type="month" value={month} onChange={handleSelectMonth} />
                        </div>
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
                            <EditableTableData onClick={handleEditBeginningBalance}>
                                {Number(data.beginningBalance).toLocaleString()}
                            </EditableTableData>
                            <td>{Number(data.monthlySummary).toLocaleString()}</td>
                        </tr>
                        </tbody>
                    </table>
                    <h4>明细</h4>
                    <StickyTable>
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
                                    <EditableTableData onClick={() => handleEditTotalAmount(it)}>
                                        {Number(it.totalAmount).toLocaleString()}
                                    </EditableTableData>
                                    <td>{Number(it.serviceFee).toLocaleString()}</td>
                                    <td>{Number(it.balanceAmount).toLocaleString()}</td>
                                    <td>{Number(it.singleAmount).toLocaleString()}</td>
                                    <EditableTableData onClick={() => handleEditQuantity(it)}>
                                        {Number(it.quantity).toLocaleString()}
                                    </EditableTableData>
                                    <td>{Number(it.handOutAmount).toLocaleString()}</td>
                                    <td>{Number(it.balanceAmountTheDay).toLocaleString()}</td>
                                </tr>
                            )
                        })}
                        </tbody>
                    </StickyTable>

                </ContentContainer>
            </Layout>
        </>
    )
}