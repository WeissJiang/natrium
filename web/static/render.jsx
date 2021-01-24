import React from '/modules/react'
import ReactDOM from '/modules/react-dom'

import Spin from '/components/spin/spin.jsx'

const { Suspense, lazy } = React

export async function render(pageUrl, selector) {
    const Page = lazy(() => import(pageUrl))
    ReactDOM.render(
        <Suspense fallback={<Spin />}>
            <Page />
        </Suspense>,
        document.querySelector(selector),
    )
}
