import React, { useState } from 'react'
import styled from 'styled-components'

import murmur3 from '../../utils/murmurhash3_32.js'

const Container = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  height: 75vh;
  font-size: 30px;

  & div {
    margin: 10px 0;
  }

  & input {
    width: 180px;
    margin: 0 10px;
    border-width: 0 0 1px 0;
    outline: none;
    font-size: 30px;
  }

`

function getNameKey(name) {
    if (!name) {
        return 0
    }
    return murmur3(name, 314) % 100
}

export default function NameKey(props) {

    const [name, setName] = useState('')

    function handleNameChange(ev) {
        const value = ev.target.value
        setName(value)
    }

    return (
        <Container>
            <div>
                <span>Name:</span>
                <input type="text" value={name} onChange={ev => handleNameChange(ev)} />
            </div>
            <div>
                <span>Name key: {getNameKey(name)}</span>
            </div>
        </Container>
    )
}


