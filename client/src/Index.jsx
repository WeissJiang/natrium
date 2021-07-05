import React from 'react'
import createReactClass from 'create-react-class'
import styled from 'styled-components'

const Container = styled.div`
  box-sizing: border-box;
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
`

const Title = styled.div`
  position: relative;
  bottom: 5rem;
  font-size: 4.5rem;
  line-height: 1;
  color: #fff;
  text-shadow: 1px 1px 3px rgb(36 37 47 / 25%);
`

const IMAGE_URL = 'https://source.unsplash.com/random/1600x900'

const ImageBackground = createReactClass({
    getInitialState() {
        setTimeout(() => {
            const image = new Image()
            image.src = IMAGE_URL
            image.addEventListener('load', () => {
                this.setState(state => {
                    return {
                        ...state,
                        backgroundImage: `url(${IMAGE_URL})`,
                        transition: 'opacity 1s ease',
                        opacity: '1',
                    }
                })
            })
        })
        return {
            backgroundPosition: 'center',
            backgroundSize: 'cover',
            position: 'absolute',
            inset: '0',
            opacity: '0',
            zIndex: '-1',
        }
    },
    render() {
        return (<div style={this.state}/>)
    }
})

export default function Index() {

    return (
        <Container>
            <Title>nano</Title>
            <ImageBackground/>
        </Container>
    )
}
