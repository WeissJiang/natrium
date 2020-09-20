import { React } from '/deps.mjs'

const { useState } = React

function Nano() {

    const [apiToken, setApiToken] = useState('')

    return (
        <div>
            <div>
                <span>API Token: </span>
                <input type="text" value={apiToken} onChange={ev => setApiToken(ev.target.value)}/>
            </div>
            <hr/>
            <div>
                nano
            </div>
        </div>
    );
}

export default Nano
