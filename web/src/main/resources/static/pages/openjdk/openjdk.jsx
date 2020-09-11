import { React } from '/deps.mjs'

function VersionSelect(props) {
    return (
        <select name="version" defaultValue="11">
            <option value="8">OpenJDK 8 (LTS)</option>
            <option value="11">OpenJDK 11 (LTS)</option>
            <option value="14">JDK 14</option>
            <option value="15">JDK 15</option>
        </select>
    )
}

function ImplSelect(props) {
    return (
        <select name="openjdk_impl" defaultValue="hotspot">
            <option value="hotspot">hotspot</option>
            <option value="j9">j9</option>
        </select>
    )
}

function OSSelect(props) {
    return (
        <select name="os" defaultValue="macos">
            <option value="macos">macOS</option>
            <option value="linux">Linux</option>
            <option value="windows">Windows</option>
        </select>
    )
}

function ArchSelect(props) {
    return (
        <select name="arch" defaultValue="x64">
            <option value="x64">x64</option>
        </select>
    )
}

function TypeSelect(props) {
    return (
        <select name="type" defaultValue="jdk">
            <option value="jdk">JDK</option>
            <option value="jre">JRE</option>
        </select>
    )
}

/**
 * @see https://api.adoptopenjdk.net/README
 */
function OpenJDK() {
    function getUrl(ev) {
        ev.preventDefault()
        const form = new FormData(ev.target)
        // get form data
        console.log('type', form.get('type'))
        console.log('version', form.get('version'))
        console.log('arch', form.get('arch'))
        console.log('os', form.get('os'))
        console.log('openjdk_impl', form.get('openjdk_impl'))
    }

    return (
        <div>
            <form onSubmit={getUrl}>
                <div>
                    Version : <VersionSelect/>
                </div>
                <div>
                    JVM: <ImplSelect/>
                </div>
                <div>
                    Operate System: <OSSelect/>
                </div>
                <div>
                    Architecture: <ArchSelect/>
                </div>
                <div>
                    Type: <TypeSelect/>
                </div>
                <button type="submit">Get Download URL</button>
            </form>
        </div>
    )
}

export default OpenJDK
