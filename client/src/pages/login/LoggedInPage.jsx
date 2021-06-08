import React from 'react'
import styled from 'styled-components'
import Button from '../../components/Button.js'
import Layout from '../../components/Layout.jsx'

const TextContainer = styled.div`
  margin-top: 25vh;
  box-sizing: border-box;
  padding: .125rem .25rem;
  font-size: 1.25rem;
  line-height: 1.75em;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: .5rem;
`

export default function LoggedInPage(props) {

    return (
        <Layout username={props.user.firstname} onLogout={props.handleLogout}>
            <TextContainer>
                <span>Hi, {props.user.firstname}</span>
                <Button onClick={props.handleLogout}>登出</Button>
            </TextContainer>
        </Layout>
    )
}