import React from 'react'
import styled from 'styled-components'
import { useDispatch, useSelector } from 'react-redux'

import { sleep } from '../../utils/schedule.js'
import message from '../../components/message.jsx'
import { CounterProvider } from './context.jsx'

const Container = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 75vh;

  & input {
    max-width: 60%;
    border-width: 0 0 .5px 0;
    outline: none;
    font-size: 30px;
    text-align: center;
  }

  & button {
    border-radius: 4px;
    font-size: 14px;
    margin: 11px 4px;
    padding: 0 16px;
    border: 0;
    line-height: 27px;
    height: 36px;
    min-width: 80px;
    text-align: center;
    outline: none;
    user-select: none;
    background-color: #f8f9fa;
    transition: color 0.3s;
  }

  & button:hover {
    box-shadow: 0 1px 1px rgba(0, 0, 0, .1);
    border: 1px solid #dadce0;
    color: #202124;
  }

  & button:active {
    color: rgba(0, 0, 0, 0.3)
  }

  & button:disabled {
    color: rgba(0, 0, 0, 0.3)
  }
`


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
        <Container>
            <input type="text" value={String(count)} onChange={handleInputChange} />
            <div>
                <button onClick={handleClickMinus}>-</button>
                <button onClick={handleClickPlus1s} disabled={plus1sButtonDisabled}>+1s</button>
            </div>
        </Container>
    )
}

export default function () {
    return (
        <CounterProvider>
            <Counter />
        </CounterProvider>
    )
}