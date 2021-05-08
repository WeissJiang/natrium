import React from 'react'
import { render } from 'react-dom'

import styles from './message.module.css'

function Notification(props) {
    return (
        <div className={styles.notification}>
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
    render(<Notification text={text} />, div)
    if (duration) {
        setTimeout(() => document.body.removeChild(div), duration)
    }
}

export default {
    info(text, options) {
        openNotification(text, options)
    }
}