import React, { useState } from 'react'
import saveAs from 'file-saver'
import { createBook } from './epub.jsx'

import '../../styles/reset.css'
import '../../styles/utilities.css'
import styles from './Album.module.css'

export default function Album(props) {

    const [loading, setLoading] = useState(false)
    const [selectedFileName, setSelectedFileName] = useState('')

    async function handleSubmit(ev) {
        ev.preventDefault()
        const form = ev.target
        const data = {
            images: form.images.files,
            filename: form.filename.value,
            title: form.title.value,
            creator: form.creator.value,
        }
        await onSubmitForm(data)
    }

    async function onSubmitForm(data = {}) {
        const { images = [], filename } = data
        setLoading(true)
        try {
            const bookBlob = await createBook({
                ...data,
                images: [...images],
            })
            saveAs(bookBlob, `${filename || new Date().getTime()}.epub`)
        } catch (err) {
            console.error(err)
        } finally {
            setLoading(false)
        }
    }

    function handleFilesSelected(ev) {
        const files = Array.from(ev.target.files)
        const filename = files.map(it => it.name).join(',')
        setSelectedFileName(filename)
    }

    return (
        <div className={styles.container}>
            <form className={styles['form-box']} onSubmit={handleSubmit}>
                <input disabled={loading} type="text" name="title" placeholder="Title" />
                <input disabled={loading} type="text" name="creator" placeholder="Creator" />
                <input disabled={loading} type="text" name="filename" placeholder="Filename" />
                <label className="cursor-pointer mt-4">
                    <span className={styles.selector}>Select Images</span>
                    <input disabled={loading} type="file" onChange={handleFilesSelected}
                           name="images" className="hidden" required multiple accept=".jpg,.jpeg,.png,.gif" />
                </label>
                <span title={selectedFileName} className={`mt-4 ${styles.tip}`}>{selectedFileName}</span>
                <button disabled={loading}>Create Album</button>
                <span>{loading && 'Book creating...'}</span>
            </form>
        </div>
    )
}
