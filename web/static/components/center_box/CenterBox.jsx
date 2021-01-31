import React from 'react'

import style from './style.module.less'

export default function CenterBox(props) {

    const className = [style.container, props.className].filter(it => !!it).join(' ')

    return (
        <div style={props.style} className={className}>
            {props.children}
        </div>
    )
}

