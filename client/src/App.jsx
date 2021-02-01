import React, { lazy, Suspense } from 'react'
import { BrowserRouter as Router, Route, Switch, useLocation } from 'react-router-dom'

import Spin from '@/components/spin/spin.jsx'

import './styles/reset.css'

const Index = lazy(() => import('@/pages/nano/Index.jsx'))
const Nano = lazy(() => import('@/pages/nano/Nano.jsx'))
const Login = lazy(() => import('@/pages/account/Login.jsx'))
const Token = lazy(() => import('@/pages/account/Token.jsx'))
const OpenJDK = lazy(() => import('@/pages/openjdk/OpenJDK.jsx'))
const Mail = lazy(() => import('@/pages/mail/Mail.jsx'))
const NameKey = lazy(() => import('@/pages/tools/NameKey.jsx'))
const Counter = lazy(() => import('@/pages/counter/Counter.jsx'))
const Sandbox = lazy(() => import('@/pages/sandbox/Sandbox.jsx'))
const Album = lazy(() => import('@/pages/album/Album.jsx'))

function NoMatch() {
    const location = useLocation()
    return (
        <div>
            <h3>
                No match for <code>{location.pathname}</code>
            </h3>
        </div>
    )
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
                    <Route path="/album"><Album /></Route>
                    <Route path="*"><NoMatch /></Route>
                </Switch>
            </Suspense>
        </Router>
    )
}

