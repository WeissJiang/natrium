import { render } from 'react-dom'
import { createElement } from 'react'
import App from './app.jsx'

render(createElement(App), document.querySelector('#app'))
