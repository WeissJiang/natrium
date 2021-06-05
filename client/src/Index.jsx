import React from 'react'
import styled from 'styled-components'

const Container = styled.div`
  box-sizing: border-box;
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-image: url(https://source.unsplash.com/random/1600x900);
  background-position: center;
  background-size: cover;
`

const Title = styled.div`
  position: relative;
  border-bottom: 5rem;
  font-size: 4.5rem;
  line-height: 1;
  color: #fff;
  text-shadow: 1px 1px 3px rgb(36 37 47 / 25%);
`

export default function Index() {

    return (
        <Container>
            <Title>nano</Title>
        </Container>
    )
}
