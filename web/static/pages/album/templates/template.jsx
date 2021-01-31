import React from 'react'
import ReactDOMServer from 'react-dom/server'
import render from '@/utils/render.mjs'

const __dirname = import.meta.url.substring(0, import.meta.url.lastIndexOf('/') + 1);
const cache = new Map

async function templated(name) {
    let item = cache.get(name)
    if (!item) {
        const response = await fetch(__dirname + name)
        item = await response.text()
        cache.set(name, item)
    }
    return item
}

export async function getContainer() {
    return templated('container.xml')
}

export async function getStylesheet() {
    return templated('stylesheet.css')
}

export async function getToc() {
    return templated('toc.xhtml')
}

export function renderToString(element) {
    return ReactDOMServer.renderToStaticMarkup(element)
}

export async function getPackage({ images, title, creator }) {
    const template = await templated('package.xml')
    const injection = renderToString(images.map((it, i) => (
        <item id={`image${i}`} href={`images/${i}.${it.name.split('.').pop()}`} media-type={it.type} />
    )))
    return render(template, {
        images: injection,
        identifier: new Date().getTime(),
        title: title || 'Title',
        creator: creator || 'Creator',
        contributor: 'Contributor',
        subject: 'Images',
        source: 'Source',
        rights: 'Public',
    })
}

export async function getPage0({ images, title }) {
    const template = await templated('page0.xhtml')
    const content = renderToString(images.map((it, i) => {
        return (
            <figure>
                <img src={`images/${i}.${it.name.split('.').pop()}`} alt={it.name} />
            </figure>
        )
    }))
    return render(template, {
        content,
        title: title || 'Title',
    })
}