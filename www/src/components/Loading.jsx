import React from 'react'
import styled from 'styled-components'

const LoadingContainer = styled.div`
  box-sizing: border-box;
  position: fixed;
  inset: 0;
  padding: 2rem;
`

const LoadingText = styled.div`
  box-sizing: border-box;
  text-align: center;
  margin-top: 30vh;
  font-size: 1.25rem;
  line-height: 1.75rem;
  font-weight: 500;
  letter-spacing: 0.025em;
`

export default function Loading(props) {

    const text = props.children || '加载中...'

    return (
        <LoadingContainer>
            <LoadingText>{text}</LoadingText>
        </LoadingContainer>
    )
}