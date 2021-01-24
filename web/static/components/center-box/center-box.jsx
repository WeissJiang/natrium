import React from '/modules/react'

import style from './style.module.less'

function CenterBox(props) {

    const className = [style.container, props.className].filter(it => !!it).join(' ')

    return (
        <div style={props.style} className={className}>
            {props.children}
        </div>
    )
}

export default CenterBox