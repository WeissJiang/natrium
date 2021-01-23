import React from '/modules/react'
import dayjs from '/modules/dayjs'

function Sandbox(props) {

    const [time, setTime] = React.useState('')

    function handleClick(){
        setTime(dayjs().format('YYYY MM-DDTHH:mm:ss SSS [Z] A'))
    }

    return (
        <div>
            <span>Sandbox: {time}</span>
            <br/>
            <button onClick={handleClick}>Click here</button>
        </div>
    )
}

export default Sandbox
