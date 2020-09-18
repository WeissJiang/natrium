import { React } from '/deps.mjs'
import { useUser } from '/hooks/user.jsx'
import { getLocalItem } from '/modules/storage.mjs'

import style from './style.module.less'

const { useState, useEffect } = React

function redirectToLoginPage() {
    const loginUrl = '/login.html'
    const backUrl = new URL(location.href).pathname
    const searchParams = new URLSearchParams({ backUrl })
    location.href = `${loginUrl}?${searchParams}`
}

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
    return result.payload
}

function Token(props) {
    const token = getLocalItem('token')
    const [loading, user] = useUser(token)
    const [tokenList, setTokenList] = useState([])

    useEffect(() => {
        if (!user) {
            return
        }
        ;(async () => {
            const tokenList = await fetchTokenList(token)
            if (tokenList) {
                setTokenList(tokenList)
            }
        })()
    }, [user])

    if (loading) {
        return <div>Loading...</div>
    }

    if (!user) {
        redirectToLoginPage()
        return <div>Redirect to login page</div>
    }

    return (
        <div>
            <span>hi, {user['firstname']}</span>
            <table className={style['t-table']}>
                <thead>
                <tr>
                    <th>NAME</th>
                    <th>LAST ACTIVE</th>
                    <th>CURRENT</th>
                </tr>
                </thead>
                <tbody>
                {tokenList.map(it => (
                    <tr key={it['name'] + it['lastActiveTime']}>
                        <td>{it['name']}</td>
                        <td>{isoToLocal(it['lastActiveTime'])}</td>
                        <td>{it['current'] && '*'}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    )
}

export default Token
