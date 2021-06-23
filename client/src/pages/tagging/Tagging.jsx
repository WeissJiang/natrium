import React, { useState } from 'react'
import styled from 'styled-components'

const Container = styled.div`
`
const Canvas = styled.div`
  position: relative;
  height: 100vh;
  background-color: #eee;
`

const BoxContainer = styled.div`
  position: absolute;
  border: 2px solid red;
  box-sizing: border-box;
`

function Box(props) {
    const style = {
        left: Math.min(props.x1, props.x2) + 'px',
        top: Math.min(props.y1, props.y2) + 'px',
        width: Math.abs(props.x2 - props.x1) + 'px',
        height: Math.abs(props.y2 - props.y1) + 'px',
    }
    return <BoxContainer style={style} />
}

export default function Tagging(props) {

    const [boxList, setBoxList] = useState([])
    const [startPosition, setStartPosition] = useState({})
    const [drawingPosition, setDrawingPosition] = useState({})

    function handleMouseDown(ev) {
        setStartPosition({
            x: ev.clientX,
            y: ev.clientY
        })
    }

    function handleMouseUp(ev) {
        setStartPosition({})
        setDrawingPosition({})
        const position = {
            key: new Date().getTime(),
            x1: startPosition.x,
            y1: startPosition.y,
            x2: ev.clientX,
            y2: ev.clientY,
        }
        setBoxList([...boxList, position])
    }

    function handleMouseMove(ev) {
        if (JSON.stringify(startPosition) === '{}') {
            return
        }
        const position = {
            x1: startPosition.x,
            y1: startPosition.y,
            x2: ev.clientX,
            y2: ev.clientY,
        }
        setDrawingPosition(position)
    }

    const drawing = JSON.stringify(drawingPosition) !== '{}'

    return (
        <Container>
            <Canvas onMouseDown={handleMouseDown}
                    onMouseUp={handleMouseUp}
                    onMouseMove={handleMouseMove}>
                {boxList.map(it => (<Box {...it} />))}
                {drawing && <Box {...drawingPosition} />}
            </Canvas>
        </Container>
    )
}