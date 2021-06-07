import React from 'react'
import styled from 'styled-components'
import Button from '../../components/Button.js'

const Container = styled.div`
  box-sizing: border-box;
  padding: 2rem;
  height: 100vh;
`

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
        <Container>
            <TextContainer>
                <span>Hi, {props.user.firstname}</span>
                <Button onClick={props.handleLogout}>登出</Button>
            </TextContainer>
        </Container>
    )
}