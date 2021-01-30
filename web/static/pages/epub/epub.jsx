import React from 'react'
import { saveAs } from 'file-saver'

import { createBook } from './create-book.jsx'

import style from './style.module.less'

export default function Epub(props) {

    const [file, setFile] = React.useState()

    async function handleClick() {
        const bookBlob = await createBook()
        saveAs(bookBlob, 'book.epub')
    }

    function handleChooseFile(ev){
        console.log(ev)
    }

    return (
        <div className={style.container}>
            <input type="file" onChange={handleChooseFile}/>
            <button onClick={handleClick}>Create book</button>
        </div>
    )
}
