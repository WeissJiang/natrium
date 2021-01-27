import JSZip from '/modules/jszip'

import {
    renderToString,
    xmlDeclaration,
    Container,
    Package,
} from './template.jsx'

export async function createBook() {
    const zip = new JSZip()
    // mimetype
    zip.file('mimetype', 'application/epub+zip');
    // meta info
    const metaInfoFolder = zip.folder('META-INF');
    metaInfoFolder.file('container.xml', xmlDeclaration + renderToString(<Container/>))
    // OEBPS
    const contentFolder = zip.folder('OEBPS');
    contentFolder.file('content.opf', xmlDeclaration + renderToString(<Package/>))
    // images
    const imageFolder = contentFolder.folder('Images')
    // text
    const textFolder = contentFolder.folder('Text')

    return await zip.generateAsync({
        type: 'blob',
        mimeType: 'application/epub+zip'
    })
}