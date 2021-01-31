import React, { useState } from 'react'
import saveAs from 'file-saver'
import { useForm } from 'react-hook-form'
import { createBook } from './create-book.jsx'

import style from './style.module.less'

export default function Epub(props) {

    const [loading, setLoading] = useState(false)
    const { register, handleSubmit, errors } = useForm()

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
        <div className={style.container}>
            <form className={style['form-box']} onSubmit={handleSubmit(onSubmitForm)}>
                <input disabled={loading} type="file" name="images" ref={register({ required: true })}
                       multiple accept=".jpg,.jpeg,.png,.gif" />
                <span>{errors.images && 'Images is required.'}</span>
                <input disabled={loading} type="text" name="title" ref={register} placeholder="Title" />
                <input disabled={loading} type="text" name="creator" ref={register} placeholder="Creator" />
                <input disabled={loading} type="text" name="filename" ref={register} placeholder="Filename" />
                <button disabled={loading}>Create book</button>
                <span>{loading && 'Book creating...'}</span>
            </form>
        </div>
    )
}
