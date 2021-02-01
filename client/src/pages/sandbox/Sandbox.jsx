import React from 'react'
import moment from 'dayjs'

export default function Sandbox(props) {

    const [time, setTime] = React.useState('')

    function handleClick(){
        setTime(moment().format('YYYY MM-DD HH:mm:ss SSS'))
    }

    return (
        <div>
            <span>Time: {time}</span>
            <br/>
            <button onClick={handleClick}>Refresh time</button>
        </div>
    )
}
