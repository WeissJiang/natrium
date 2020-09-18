import { React, ReactDOM } from '/deps.mjs'

const { Suspense, lazy } = React

export default async function (appUrl, elementId) {
    const App = lazy(() => import(appUrl))
    ReactDOM.render(
        <Suspense fallback={null}>
            <App/>
        </Suspense>, document.getElementById(elementId))
}

