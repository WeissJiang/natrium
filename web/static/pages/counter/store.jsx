import Redux from '/modules/redux'
import ReactRedux from '/modules/react-redux'

const { Provider } = ReactRedux

function reducer(state, action) {
    if (action.type === 'setCount') {
        return {
            ...state,
            count: action.payload
        }
    }

    return state
}

const initialState = { count: 7 }

export const store = Redux.createStore(reducer, initialState)

export function CounterProvider(props) {
    return (
        <Provider store={store}>
            {props.children}
        </Provider>
    )
}