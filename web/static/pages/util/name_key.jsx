import React from '/modules/react'

import murmur3 from '/modules/murmurhash3_32.mjs'

const { useState } = React

function getNameKey(name) {
    if (!name) {
        return 0
    }
    return murmur3(name, 314) % 100
}

function NameKey(props) {

    const [name, setName] = useState('')

    function handleNameChange(ev) {
        const value = ev.target.value.trim()
        setName(value)
    }

    return (
        <div>
            <span>Name: </span>
            <input type="text" value={name} onChange={ev => handleNameChange(ev)}/>
            <span>, &nbsp; Name key: {getNameKey(name)}.</span>
        </div>
    )
}

export default NameKey
