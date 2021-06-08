import React from 'react'
import styled from 'styled-components'
import Button from '../../components/Button.js'

const Container = styled.div`
  box-sizing: border-box;
  padding: 2rem;
  height: 100vh;
`

const Form = styled.form`
  box-sizing: border-box;
  margin-top: 25vh;
  display: flex;
  flex-direction: column;
  align-items: center;
`

const UsernameInput = styled.label`
  box-sizing: border-box;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: .5rem;
  padding: .125rem .25rem;
  font-size: 1.25rem;
  line-height: 1.75em;

  & > input {
    border: 1px solid #000;
    border-radius: 2px;
    font-size: 1.25rem;
    line-height: 1.75rem;
    outline: none;
    padding: .25rem;
  }
`

const LoginButton = styled(Button)`
  margin-top: 1rem;
`

export default function LoginFormPage(props) {

    return (
        <Container>
            <Form onSubmit={props.handleSubmit}>
                <UsernameInput>
                    <span>请输入电报用户名：</span>
                    <input type="text" name="username" required />
                </UsernameInput>
                <LoginButton type="submit">登录</LoginButton>
            </Form>
        </Container>
    )
}