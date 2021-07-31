import React, { useState } from 'react'

export default function PriceInput(props) {

    const [editing, setEditing] = useState(false)
    const [value, setValue] = useState('')

    const formattedValue = value && Number(value).toLocaleString()

    return (
        <input type="text"
               value={editing ? value : formattedValue}
               inputMode="numeric"
               onFocus={() => setEditing(true)}
               onBlur={() => setEditing(false)}
               onChange={(ev) => setValue(ev.target.value)}
               placeholder="Enter"
               pattern="[0-9]*"/>
    )
}