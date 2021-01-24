import React from '/modules/react'
import ReactRedux from '/modules/react-redux'
import { sleep } from '/utils/schedule.mjs'
import CenterBox from '/components/center-box/center-box.jsx'

import { CounterProvider } from './store.jsx'

import style from './style.module.less'

function Counter(props) {

    const [plus1sButtonDisabled, setPlus1sButtonDisabled] = React.useState(false)

    function handleInputChange(ev) {
        props.dispatch({
            type: 'setCount',
            payload: Number(ev.target.value) || 0
        })
    }
    function handleClickMinus() {
        props.dispatch({
            type: 'setCount',
            payload: props.count - 1
        })
    }

    async function handleClickPlus1s() {
        setPlus1sButtonDisabled(true)
        await sleep(1000)
        props.dispatch({
            type: 'setCount',
            payload: props.count + 1
        })
        setPlus1sButtonDisabled(false)
    }

    return (
        <CenterBox className={style.container}>
            <input type="text" value={props.count} onChange={handleInputChange} />
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

const WrappedCounter = ReactRedux.connect(mapStateToProps)(Counter)

export default function () {
    return (
        <CounterProvider>
            <WrappedCounter />
        </CounterProvider>
    )
}