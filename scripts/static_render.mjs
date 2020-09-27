import React from 'http://localhost:8080/modules/react'
import ReactDOMServer from 'http://localhost:8080/modules/react-dom-server'

import Index from 'http://localhost:8080/pages/nano/index.jsx'

const html = ReactDOMServer.renderToString(React.createElement(Index, null))
console.log(html)

// deno run --no-check --reload scripts/static_render.mjs
