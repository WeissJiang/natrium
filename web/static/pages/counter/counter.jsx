import React from '/modules/react'
import ReactRedux from '/modules/react-redux'
import { sleep } from '/utils/schedule.mjs'

import { CounterProvider } from './store.jsx'

function Counter(props) {

    const [plus1sButtonDisabled, setPlus1sButtonDisabled] = React.useState(false)

    function handleInputChange(ev) {
        props.dispatch({
            type: 'setCount',
            payload: ev.target.value
        })
    }
    function handleClickPlus() {
        props.dispatch({
            type: 'setCount',
            payload: props.count + 1
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
        <div>
            <input type="text" value={props.count} onChange={handleInputChange} />
            <br />
            <button onClick={handleClickPlus}>+</button>
            <button onClick={handleClickPlus1s} disabled={plus1sButtonDisabled}>+1s</button>
        </div>
    )
}

function mapStateToProps(state) {
    return { ...state }
}

const WrappedCounter = ReactRedux.connect(mapStateToProps)(Counter)

export default function () {
    return (
        <CounterProvider>
            <WrappedCounter />
        </CounterProvider>
    )
}