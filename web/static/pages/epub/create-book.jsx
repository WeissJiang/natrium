import JSZip from '/modules/jszip'

import { getContainer, getPackage, getPage0, getToc, getStylesheet } from './templates/template.jsx'

export async function createBook(images = []) {
    const zip = new JSZip()
    // mimetype
    zip.file('mimetype', 'application/epub+zip');
    // meta info
    const metaInfoFolder = zip.folder('META-INF');
    metaInfoFolder.file('container.xml', await getContainer())
    // OPS
    const contentFolder = zip.folder('OPS');
    contentFolder.file('package.opf', await getPackage({ images }))
    // text
    contentFolder.file('toc.xhtml', await getToc())
    contentFolder.file('page0.xhtml', await getPage0({ images }))
    // style
    contentFolder.file('stylesheet.css', await getStylesheet())
    // images
    const imageFolder = contentFolder.folder('images')

    return await zip.generateAsync({ type: 'blob', mimeType: 'application/epub+zip' })
}