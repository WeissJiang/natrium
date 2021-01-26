import React from '/modules/react'
import ReactDOMServer from '/modules/react-dom-server'

import Template from './template.jsx'

function renderToString() {
    return ReactDOMServer.renderToStaticMarkup(<Template/>)
}

export default function Epub(props) {
    console.log(renderToString())

    return (
        <div>ePub</div>
    )
}