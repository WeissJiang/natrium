import React from 'react'
import styled, { keyframes } from 'styled-components'

const spin = keyframes`
  0%, 100% {
    transform: translate(0);
  }
  25% {
    transform: translate(160%);
  }
  50% {
    transform: translate(160%, 160%);
  }
  75% {
    transform: translate(0, 160%);
  }
`


const SpinContainer = styled.div`
  position: fixed;
  top: 36%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 40px;
  height: 40px;

  & > hr {
    border: 0;
    margin: 0;
    width: 40%;
    height: 40%;
    position: absolute;
    border-radius: 50%;
    animation: ${spin} 2s ease infinite;

    &:first-child {
      background: #19A68C;
      animation-delay: -1.5s;
    }

    &:nth-child(2) {
      background: #F63D3A;
      animation-delay: -1s;
    }

    &:nth-child(3) {
      background: #FDA543;
      animation-delay: -0.5s;
    }

    &:last-child {
      background: #193B48;
    }
  }
`

export default function Spin(props) {

    return (
        <SpinContainer>
            <hr />
            <hr />
            <hr />
            <hr />
        </SpinContainer>
    )
}