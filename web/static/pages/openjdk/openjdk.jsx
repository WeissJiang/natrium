import React from '/modules/react'
import { getOpenJDKInfo2 } from './api.mjs'

import style from './style.module.less'

function VersionSelect() {
    return (
        <select name="version" defaultValue="11">
            <option value="8">OpenJDK 8 (LTS)</option>
            <option value="9">OpenJDK 9 (MTS)</option>
            <option value="10">OpenJDK 10 (MTS)</option>
            <option value="11">OpenJDK 11 (LTS)</option>
            <option value="12">OpenJDK 12 (MTS)</option>
            <option value="13">OpenJDK 13 (MTS)</option>
            <option value="14">OpenJDK 14 (MTS)</option>
            <option value="15">OpenJDK 15 (MTS)</option>
            <option value="16">OpenJDK 16 (MTS)</option>
            <option value="17">OpenJDK 17 (LTS)</option>
        </select>
    )
}

function ImplSelect() {
    return (
        <select name="openjdk_impl" defaultValue="hotspot">
            <option value="hotspot">HotSpot</option>
            <option value="openj9">OpenJ9</option>
        </select>
    )
}

function OSSelect() {
    return (
        <select name="os" defaultValue="linux">
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
            <option value="aarch64">aarch64</option>
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
    const link = (url, name) => <a href={url}>{name || url && new URL(url).pathname.split('/').pop()}</a>
    const b2mb = size => size && (((size | 0) / 1024 / 1024).toFixed(2) + 'MB')
    return (
        <table>
            <tbody>
            <tr>
                <td>Release:</td>
                <td>{link(props.release_link, props.release_name)}</td>
            </tr>
            <tr>
                <td>Binary:</td>
                <td>{link(props.binary_link, props.binary_name)}</td>
            </tr>
            <tr>
                <td>Binary checksum:</td>
                <td>{link(props.checksum_link)}</td>
            </tr>
            <tr>
                <td>Binary size:</td>
                <td>{b2mb(props.binary_size)}</td>
            </tr>
            <tr>
                <td>Installer:</td>
                <td>{link(props.installer_link, props.installer_name)}</td>
            </tr>
            <tr>
                <td>Installer checksum:</td>
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

        const params = {
            release: 'latest',
            openjdk_impl: form.get('openjdk_impl'),
            os: form.get('os'),
            arch: form.get('arch'),
            type: form.get('type'),
            version: form.get('version'),
        }

        try {
            setLoading(true)
            setInfo(false)
            const data = await getOpenJDKInfo2(params)
            setInfo(data)
            setLoading(false)
        } catch (ex) {
            alert(ex.message)
            setInfo(false)
            setLoading(false)
        }

    }

    return (
        <div className={style.container}>
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
