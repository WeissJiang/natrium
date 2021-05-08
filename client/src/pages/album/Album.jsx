import React, { useState } from 'react'
import saveAs from 'file-saver'
import { createBook } from './epub.jsx'

import styles from './Album.module.css'

export default function Album(props) {

    const [loading, setLoading] = useState(false)

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

    return (
        <div className={styles.container}>
            <form className={styles['form-box']} onSubmit={handleSubmit}>
                <input disabled={loading} type="file" name="images" required multiple accept=".jpg,.jpeg,.png,.gif" />
                <input disabled={loading} type="text" name="title" placeholder="Title" />
                <input disabled={loading} type="text" name="creator" placeholder="Creator" />
                <input disabled={loading} type="text" name="filename" placeholder="Filename" />
                <button disabled={loading}>Create Album</button>
                <span>{loading && 'Book creating...'}</span>
            </form>
        </div>
    )
}
