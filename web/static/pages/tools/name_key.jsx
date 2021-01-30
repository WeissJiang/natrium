import React,{ useState } from 'react'

import murmur3 from '@/utils/murmurhash3_32.mjs'

import style from './style.module.less'

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
        <div className={style.container}>
            <div>
                <span>Name:</span>
                <input type="text" value={name} onChange={ev => handleNameChange(ev)} />
            </div>
            <div>
                <span>Name key: {getNameKey(name)}</span>
            </div>
        </div>
    )
}


