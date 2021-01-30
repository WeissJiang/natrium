import React from 'react'
import ReactDOM from 'react-dom'

import style from './style.module.less'

function Notification(props) {
    return (
        <div className={style.notification}>
            <span>{props.text}</span>
        </div>
    )
}

function getContainer() {
    const div = document.createElement('div')
    document.body.appendChild(div)
    return div
}

function openNotification(text, { duration } = { duration: 3000 }) {
    const div = getContainer()
    ReactDOM.render(<Notification text={text} />, div);
    if (duration) {
        setTimeout(() => document.body.removeChild(div), duration)
    }
}

export default {
    info(text, options) {
        openNotification(text, options)
    }
}