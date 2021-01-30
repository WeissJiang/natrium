import React from 'react'
import { createStore } from 'redux'
import { Provider } from 'react-redux'

function reducer(state, action) {
    if (action.type === 'setCount') {
        return {
            ...state,
            count: action.payload
        }
    }
    return state
}

const initialState = { count: 7n }

export const store = createStore(reducer, initialState)

export function CounterProvider(props) {
    return (
        <Provider store={store}>
            {props.children}
        </Provider>
    )
}