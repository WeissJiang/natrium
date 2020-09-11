import { React } from '/deps.mjs'
import { assertEquals } from '/modules/assertions.mjs'

function VersionSelect() {
    return (
        <select name="version" defaultValue="11">
            <option value="8">OpenJDK 8 (LTS)</option>
            <option value="11">OpenJDK 11 (LTS)</option>
            <option value="14">JDK 14</option>
            <option value="15">JDK 15</option>
        </select>
    )
}

function ImplSelect() {
    return (
        <select name="openjdk_impl" defaultValue="hotspot">
            <option value="hotspot">hotspot</option>
            <option value="openj9">j9</option>
        </select>
    )
}

function OSSelect() {
    return (
        <select name="os" defaultValue="macos">
            <option value="mac">macOS</option>
            <option value="linux">Linux</option>
            <option value="windows">Windows</option>
            <option value="solaris">Solaris</option>
            <option value="aix">AIX</option>
        </select>
    )
}

function ArchSelect() {
    return (
        <select name="arch" defaultValue="x64">
            <option value="x64">x64</option>
        </select>
    )
}

function TypeSelect() {
    return (
        <select name="type" defaultValue="jdk">
            <option value="jdk">JDK</option>
            <option value="jre">JRE</option>
        </select>
    )
}

function InfoTable(props) {
    const link = url => <a href={url}>{url}</a>
    const b2mb = size => ((size | 0) / 1024 / 1024).toFixed(2) + 'MB'
    return (
        <table>
            <tbody>
            <tr>
                <td>Release name:</td>
                <td>{props.release_name}</td>
            </tr>
            <tr>
                <td>Release link:</td>
                <td>{link(props.release_link)}</td>
            </tr>
            <tr>
                <td>Binary name:</td>
                <td>{props.binary_name}</td>
            </tr>
            <tr>
                <td>Binary link:</td>
                <td>{link(props.binary_link)}</td>
            </tr>
            <tr>
                <td>Checksum link:</td>
                <td>{link(props.checksum_link)}</td>
            </tr>
            <tr>
                <td>Binary size:</td>
                <td>{b2mb(props.binary_size)}</td>
            </tr>
            <tr>
                <td>Installer name:</td>
                <td>{props.installer_name}</td>
            </tr>
            <tr>
                <td>Installer link:</td>
                <td>{link(props.installer_link)}</td>
            </tr>
            <tr>
                <td>Installer checksum link:</td>
                <td>{link(props.installer_checksum_link)}</td>
            </tr>
            <tr>
                <td>Installer size:</td>
                <td>{b2mb(props.installer_size)}</td>
            </tr>
            <tr>
                <td>Updated at:</td>
                <td>{props.updated_at}</td>
            </tr>
            </tbody>
        </table>
    )
}

/**
 * @see https://api.adoptopenjdk.net/README
 */
function OpenJDK() {

    const [loading, setLoading] = React.useState(false)
    const [info, setInfo] = React.useState(false)

    async function getUrl(ev) {
        ev.preventDefault()
        const form = new FormData(ev.target)
        // get form data
        const searchParams = new URLSearchParams({
            release: 'latest',
            openjdk_impl: form.get('openjdk_impl'),
            os: form.get('os'),
            arch: form.get('arch'),
            type: form.get('type'),
        })

        try {
            setLoading(true)
            setInfo(false)
            const response = await fetch(`https://api.adoptopenjdk.net/v2/info/releases/openjdk${form.get('version')}?${searchParams}`)
            assertEquals(response.status, 200, `response is ${response.status}`)
            const data = await response.json()
            setInfo(data)
            setLoading(false)
        } catch (ex) {
            alert(ex.message)
            setInfo(false)
            setLoading(false)
        }

    }

    return (
        <div>
            <form onSubmit={getUrl}>
                <table>
                    <tbody>
                    <tr>
                        <td>Version:</td>
                        <td><VersionSelect/></td>
                    </tr>
                    <tr>
                        <td>JVM:</td>
                        <td><ImplSelect/></td>
                    </tr>
                    <tr>
                        <td>Operate System:</td>
                        <td><OSSelect/></td>
                    </tr>
                    <tr>
                        <td>Architecture:</td>
                        <td><ArchSelect/></td>
                    </tr>
                    <tr>
                        <td>Type:</td>
                        <td><TypeSelect/></td>
                    </tr>
                    <tr>
                        <td/>
                        <td>
                            <button type="submit" disabled={loading}>Get JDK Info</button>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </form>
            {(function () {
                if (info) {
                    return <InfoTable {...Object.assign({}, info, info.binaries[0])}/>
                } else if (loading) {
                    return <span>loading...</span>
                }
            })()}
        </div>
    )
}

export default OpenJDK
