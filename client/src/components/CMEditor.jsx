import React, { Component, createRef } from 'react'
import styled from 'styled-components'
import { EditorView } from '@codemirror/view'
import { EditorState } from '@codemirror/state'

const Container = styled.div`
    
`

export default class CMEditor extends Component {

    constructor(props) {
        super(props);
        this.containerRef = createRef()
    }

    componentDidMount() {
        this.editor = new EditorView({
            state: EditorState.create({}),
            parent: this.containerRef.current,
        })
    }

    render() {
        return (
            <Container ref={this.containerRef}/>
        )
    }
}
