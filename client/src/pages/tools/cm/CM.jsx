import React from 'react'
import styled from 'styled-components'
import CMEditor from '../../../components/CMEditor.jsx'

const Container = styled.div`

`

export default function CM(props) {

    return (
        <Container>
            <CMEditor/>
        </Container>
    )
}