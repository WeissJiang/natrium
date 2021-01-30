import React from 'react'
import ReactDOMServer from 'react-dom/server'
import render from '@/utils/render.mjs'

export function renderToString(element) {
    return ReactDOMServer.renderToStaticMarkup(element)
}

const templateCache = new Map

async function getTemplate(name) {
    let template = templateCache.get(name)
    if (!template) {
        const response = await fetch(`/pages/epub/templates/${name}`)
        template = await response.text()
        templateCache.set(name, template)
    }
    return template
}

export async function getContainer() {
    return getTemplate('container.xml')
}

export async function getStylesheet() {
    return getTemplate('stylesheet.css')
}

export async function getPackage() {
    const template = await getTemplate('package.xml')
    return render(template, { images: '' })
}

export async function getPage0() {
    const template = await getTemplate('page0.xhtml')
    return render(template, { content: 'hello world' })
}

export async function getToc() {
    return getTemplate('toc.xhtml')
}