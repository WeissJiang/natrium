import React from 'react'
import { connect } from 'react-redux'
import { sleep } from '@/utils/schedule.mjs'
import CenterBox from '@/components/center-box/center-box.jsx'
import message from '@/components/message/message.jsx'

import { CounterProvider } from './store.jsx'

import style from './style.module.less'

function convertToBitInt(value, def) {
    try {
        return BigInt(value)
    } catch (err) {
        message.info(err.message)
        return def
    }
}

function Counter(props) {

    const [plus1sButtonDisabled, setPlus1sButtonDisabled] = React.useState(false)

    function handleInputChange(ev) {
        props.dispatch({
            type: 'setCount',
            payload: convertToBitInt(ev.target.value, props.count)
        })
    }
    function handleClickMinus() {
        props.dispatch({
            type: 'setCount',
            payload: props.count - 1n
        })
    }

    async function handleClickPlus1s() {
        setPlus1sButtonDisabled(true)
        await sleep(1000)
        props.dispatch({
            type: 'setCount',
            payload: props.count + 1n
        })
        setPlus1sButtonDisabled(false)
    }

    return (
        <CenterBox className={style.container}>
            <input type="text" value={String(props.count)} onChange={handleInputChange} />
            <div>
                <button onClick={handleClickMinus}>-</button>
                <button onClick={handleClickPlus1s} disabled={plus1sButtonDisabled}>+1s</button>
            </div>
        </CenterBox>
    )
}

function mapStateToProps({ count }) {
    return { count }
}

const WrappedCounter = connect(mapStateToProps)(Counter)

export default function () {
    return (
        <CounterProvider>
            <WrappedCounter />
        </CounterProvider>
    )
}