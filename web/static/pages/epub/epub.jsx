import React from '/modules/react'
import { saveAs } from '/utils/file_saver.mjs'

import { createBook } from './create-book.jsx'

import style from './style.module.less'

export default function Epub(props) {

   async function handleClick() {
        const bookBlob = await createBook()
        saveAs(bookBlob, 'book.epub')
   }

    return (
        <div className={style.container}>
            <button onClick={handleClick}>Create book</button>
        </div>
    )
}
