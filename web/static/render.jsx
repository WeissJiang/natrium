import React from '/modules/react'
import ReactDOM from '/modules/react-dom'

const { Suspense, lazy } = React

export default async function (pageUrl, elementId) {
    const Page = lazy(() => import(pageUrl))
    ReactDOM.render(
        <Suspense fallback={null}>
            <Page/>
        </Suspense>, document.getElementById(elementId))
}
