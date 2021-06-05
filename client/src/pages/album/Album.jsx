import React, { useState } from 'react'
import saveAs from 'file-saver'
import styled from 'styled-components'

import { createBook } from './epub.js'

const Container = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  height: 75vh;

  & .form-box {
    height: 70%;
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
  }

  & input {
    margin: .5em;
    max-width: 60%;
    border-width: 0 0 .5px 0;
    outline: none;
    font-size: 22px;
    text-align: center;
  }

  & span.selector {
    box-sizing: border-box;
    border-radius: 4px;
    padding: 8px 12px;
    border: 1px dashed #999;
    font-weight: bold;
  }

  & span.tip {
    color: #999999;
    font-size: 14px;
    max-width: 50%;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    cursor: default;
  }

  & button {
    border-radius: 4px;
    font-family: arial, sans-serif;
    font-size: 14px;
    margin: 16px 4px;
    padding: 0 16px;
    border: 1px solid rgba(0, 0, 0, 0);
    line-height: 27px;
    width: 180px;
    height: 40px;
    text-align: center;
    outline: none;
    user-select: none;
    background-color: #f8f9fa;
    transition: color 0.3s;
  }

  & button:hover {
    box-shadow: 0 1px 1px rgba(0, 0, 0, .1);
    border: 1px solid #dadce0;
    color: #202124;
  }

  & button:active {
    color: rgba(0, 0, 0, 0.3)
  }

  & button:disabled {
    color: rgba(0, 0, 0, 0.3)
  }

  & button {
    width: 180px;
  }
`

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
        <Container>
            <form className="form-box" onSubmit={handleSubmit}>
                <input disabled={loading} type="text" name="title" placeholder="Title" />
                <input disabled={loading} type="text" name="creator" placeholder="Creator" />
                <input disabled={loading} type="text" name="filename" placeholder="Filename" />
                <label style={{ cursor: 'pointer', marginTop: '1rem', }}>
                    <span className="selector">Select Images</span>
                    <input style={{ display: 'none' }} disabled={loading} type="file" onChange={handleFilesSelected}
                           name="images" required multiple accept=".jpg,.jpeg,.png,.gif" />
                </label>
                <span title={selectedFileName} style={{ marginTop: '1rem', }} className="tip">{selectedFileName}</span>
                <button disabled={loading}>Create Album</button>
                <span>{loading && 'Book creating...'}</span>
            </form>
        </Container>
    )
}
