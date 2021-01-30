import React, { lazy, Suspense } from 'react'
import { BrowserRouter as Router, Route, Switch, useLocation } from 'react-router-dom'

import Spin from '@/components/spin/spin.jsx'

const Index = lazy(() => import('@/pages/nano/index.jsx'))
const Nano = lazy(() => import('@/pages/nano/nano.jsx'))
const Login = lazy(() => import('@/pages/account/login.jsx'))
const Token = lazy(() => import('@/pages/account/token.jsx'))
const OpenJDK = lazy(() => import('@/pages/openjdk/openjdk.jsx'))
const Mail = lazy(() => import('@/pages/mail/mail.jsx'))
const NameKey = lazy(() => import('@/pages/tools/name_key.jsx'))
const Counter = lazy(() => import('@/pages/counter/counter.jsx'))
const Sandbox = lazy(() => import('@/pages/sandbox/sandbox.jsx'))
const Epub = lazy(() => import('@/pages/epub/epub.jsx'))

function NoMatch() {
    const location = useLocation()
    return (
        <div>
            <h3>
                No match for <code>{location.pathname}</code>
            </h3>
        </div>
    );
}


export default function App() {
    return (
        <Router>

            <Suspense fallback={<Spin />}>
                <Switch>
                    <Route path="/" exact><Index /></Route>
                    <Route path="/nano"><Nano /></Route>
                    <Route path="/login"><Login /></Route>
                    <Route path="/token"><Token /></Route>
                    <Route path="/openjdk"><OpenJDK /></Route>
                    <Route path="/mail"><Mail /></Route>
                    <Route path="/tools/name-key"><NameKey /></Route>
                    <Route path="/counter"><Counter /></Route>
                    <Route path="/sandbox"><Sandbox /></Route>
                    <Route path="/epub"><Epub /></Route>
                    <Route path="*"><NoMatch /></Route>
                </Switch>
            </Suspense>
        </Router>
    )
}

