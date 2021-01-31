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
            <form onSubmit={handleSubmit(onSubmitForm)}>
                <input type="file" name="images" ref={register({ required: true })} disabled={loading} multiple accept=".jpg,.jpeg,.png,.gif" />
                {errors.images && 'Images is required.'}
                <input type="text" name="title" ref={register} placeholder="Title" />
                <input type="text" name="creator" ref={register} placeholder="Creator" />
                <input type="text" name="filename" ref={register} placeholder="Filename" />
                <input disabled={loading} type="submit" value="Create book" />
                {loading && 'Book creating...'}
            </form>
        </div>
    )
}
