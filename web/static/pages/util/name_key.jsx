import { React } from '/deps.mjs'

const { useState } = React

function getNameKey(name) {
    if (!name) {
        return 0
    }
    return name.split('').reduce((a, c) => a + c.charCodeAt(0), 0) % 100
}

function NameKey(props) {

    const [name, setName] = useState('')

    return (
        <div>
            <span>Name: </span>
            <input type="text" value={name} onChange={ev => setName(ev.target.value.trim())}/>
            <span>, &nbsp; Name key: {getNameKey(name)}.</span>
        </div>
    )
}

export default NameKey
