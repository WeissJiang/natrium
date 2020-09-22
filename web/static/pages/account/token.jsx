import { React } from '/deps.mjs'
import { useUser, useToken } from '/hooks/account.jsx'

import style from './style.module.less'

const { useState, useEffect } = React

function redirectToLoginPage() {
    const loginUrl = '/login'
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
    const [tokenList, setTokenList] = useState([])

    const { token } = useToken()
    const { loading, user } = useUser(token)

    useEffect(() => {
        if (!user) {
            return
        }
        ;(async () => {
            const tokenList = await fetchTokenList(token)
            setTokenList(tokenList)
        })()
    }, [user])

    if (loading) {
        return <div>Loading...</div>
    }

    if (!user) {
        redirectToLoginPage()
        return <div>Redirecting to login page...</div>
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
        const tokenList = await fetchTokenList(token)
        setTokenList(tokenList)
    }

    return (
        <div>
            <span>hi, {user['firstname']}</span>
            <table className={style['t-table']}>
                <thead>
                <tr>
                    <th/>
                    <th>NAME</th>
                    <th>LAST ACTIVE</th>
                    <th>OPERATION</th>
                </tr>
                </thead>
                <tbody>
                {tokenList.map(it => (
                    <tr key={it['id']}>
                        <td>{it['current'] && '*'}</td>
                        <td>{it['name']}</td>
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
