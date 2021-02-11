import React from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { sleep } from '@/utils/schedule.mjs'
import CenterBox from '@/components/CenterBox/CenterBox.jsx'
import message from '@/components/message/message.jsx'

import { CounterProvider } from './store.jsx'

import style from './style.module.less'

function convertToBigInt(value, def) {
    try {
        return BigInt(value)
    } catch (err) {
        message.info(err.message)
        return def
    }
}

function Counter(props) {

    const count = useSelector(state => state.count)
    const dispatch = useDispatch()

    const [plus1sButtonDisabled, setPlus1sButtonDisabled] = React.useState(false)

    function handleInputChange(ev) {
        dispatch({
            type: 'setCount',
            payload: convertToBigInt(ev.target.value, count)
        })
    }

    function handleClickMinus() {
        dispatch({
            type: 'setCount',
            payload: count - 1n
        })
    }

    async function handleClickPlus1s() {
        setPlus1sButtonDisabled(true)
        await sleep(1000)
        dispatch({
            type: 'setCount',
            payload: count + 1n
        })
        setPlus1sButtonDisabled(false)
    }

    return (
        <CenterBox className={style.container}>
            <input type="text" value={String(count)} onChange={handleInputChange} />
            <div>
                <button onClick={handleClickMinus}>-</button>
                <button onClick={handleClickPlus1s} disabled={plus1sButtonDisabled}>+1s</button>
            </div>
        </CenterBox>
    )
}

export default function () {
    return (
        <CounterProvider>
            <Counter />
        </CounterProvider>
    )
}