import React from 'react'
import styled from 'styled-components'

const Container = styled.div`
  box-sizing: border-box;
  height: 100vh;
  overflow: auto;
`

const HeaderContainer = styled.div`
  box-sizing: border-box;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: .25rem 1rem;
  min-height: 40px;
  font-size: 1.125rem;
  line-height: 1.75rem;
  border-bottom: 1px solid #000;
  position: sticky;
  top: 0;
  background-color: #fff;

  & > .right {
    display: flex;
    align-items: center;
    gap: .5rem;

    & > span.username {
      cursor: default;
    }

    & > button.logout {
      font-size: 1rem;
      line-height: 1.5rem;
      box-sizing: border-box;
      border: 1px solid #000;
      border-radius: 2px;
      background-color: #fff;
      transition: color, background-color 200ms;
      cursor: pointer;

      &:hover {
        color: #fff;
        background-color: #000;
      }

      &:active {
        transform: scale(0.95, 0.95);
        color: #fff;
        background-color: #000;
      }
    }
  }
`

const ContentContainer = styled.div`
  box-sizing: border-box;
`

const ContentLoadingContainer = styled.div`
  box-sizing: border-box;
  text-align: center;
  margin-top: 30vh;
  font-size: 1.25rem;
  line-height: 1.75rem;
  font-weight: 500;
  letter-spacing: 0.025em;
`

function ContentLoading(props) {

    return (
        <ContentLoadingContainer>加载中...</ContentLoadingContainer>
    )
}

export default function Layout(props) {

    return (
        <Container>
            <HeaderContainer>
                <div className="left" />
                <div className="right">
                    <span className="username">{props.username}</span>
                    <button onClick={props.onLogout} className="logout">登出</button>
                </div>
            </HeaderContainer>
            <ContentContainer>
                {props.loading ? <ContentLoading /> : props.children}
            </ContentContainer>
        </Container>
    )
}