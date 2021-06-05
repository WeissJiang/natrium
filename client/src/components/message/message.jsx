import React from 'react'
import { render } from 'react-dom'
import styled from 'styled-components'

const NotificationContainer = styled.div`
  position: fixed;
  display: flex;
  justify-content: center;
  width: 100%;
  top: 20px;

  & span {
    display: inline-block;
    padding: 10px 16px;
    background: #fff;
    border-radius: 2px;
    box-shadow: 0 3px 6px -4px rgba(0, 0, 0, .12), 0 6px 16px 0 rgba(0, 0, 0, .08), 0 9px 28px 8px rgba(0, 0, 0, .05);
    pointer-events: all;
    font-family: arial, sans-serif;
    font-size: 14px;
    font-variant: tabular-nums;
    font-feature-settings: 'tnum';
    color: rgba(0, 0, 0, .85);
  }
`

function Notification(props) {
    return (
        <NotificationContainer>
            <span>{props.text}</span>
        </NotificationContainer>
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