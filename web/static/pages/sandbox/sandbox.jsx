import React from '/modules/react'
import moment from '/modules/dayjs'

function Sandbox(props) {

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

export default Sandbox
