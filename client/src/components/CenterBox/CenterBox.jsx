import React from 'react'

import styles from './CenterBox.module.css'

export default function CenterBox(props) {

    const className = [styles.container, props.className].filter(it => !!it).join(' ')

    return (
        <div style={props.style} className={className}>
            {props.children}
        </div>
    )
}

