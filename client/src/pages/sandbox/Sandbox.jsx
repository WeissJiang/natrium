import React, { useState } from 'react'

export default function Sandbox(props) {

    const [time, setTime] = useState('')

    function handleClick() {
        setTime(new Date().toISOString)
    }

    return (
        <div>
            <span>Time: {time}</span>
            <br />
            <button onClick={handleClick}>Refresh Time</button>
        </div>
    )
}

