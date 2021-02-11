import React from 'react'
import { createStore } from 'redux'
import { Provider } from 'react-redux'

const initialState = { count: 7n }

function reducer(state = initialState, action) {
    if (action.type === 'setCount') {
        return {
            ...state,
            count: action.payload
        }
    }
    return state
}

export const store = createStore(reducer)

export function CounterProvider(props) {
    return (
        <Provider store={store}>
            {props.children}
        </Provider>
    )
}