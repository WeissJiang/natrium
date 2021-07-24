import React, { createRef } from 'react'
import styled from 'styled-components'
import createReactClass from 'create-react-class'
import { EditorView } from '@codemirror/view'
import { EditorState } from '@codemirror/state'

const Container = styled.div`

`

const CMEditor = createReactClass({
    containerRef: createRef(),
    componentDidMount() {
        this.editor = new EditorView({
            state: EditorState.create({}),
            parent: this.containerRef.current,
        })
    },
    render() {
        return (
            <Container ref={this.containerRef}/>
        )
    }
})

export default CMEditor