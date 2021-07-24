import React from 'react'
import styled from 'styled-components'

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

  & a {
    color: inherit;
    box-sizing: border-box;
    text-decoration: inherit;
    margin: 0 .25rem;
    padding: .125rem .25rem;
    font-weight: 500;

    &:hover {
      color: #fff;
      background-color: #000;
    }
  }
`

export default function VerifyingPage(props) {
    return (
        <Container>
            <TextContainer>
                <span>
                    验证码: <strong>{props.verificationCode}</strong>
                </span>
                <span>
                    请发送到<a href="https://t.me/nano_telegram_bot" target="_blank">@nano_telegram_bot</a>验证登陆
                </span>
            </TextContainer>
        </Container>
    )
}