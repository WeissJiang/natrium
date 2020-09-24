import { React, ReactDOM } from '/deps.mjs'

const { Suspense, lazy } = React

export default async function (pageUrl, elementId) {
    const Page = lazy(() => import(pageUrl))
    ReactDOM.render(
        <Suspense fallback={null}>
            <Page/>
        </Suspense>, document.getElementById(elementId))
}
