import React from '/modules/react'
import ReactDOMServer from '/modules/react-dom-server'

export function renderToString(element) {
    return ReactDOMServer.renderToStaticMarkup(element)
}

export const xmlDeclaration = '<?xml version="1.0" encoding="UTF-8"?>' + '\n'

export function Container() {
    return (
        <container version="1.0" xmlns="urn:oasis:names:tc:opendocument:xmlns:container">
            <rootfiles>
                <rootfile full-path="OEBPS/content.opf" media-type="application/oebps-package+xml"/>
            </rootfiles>
        </container>
    )
}

export function Package() {
    return (
        <package version="3.0" unique-identifier="book-id" xmlns="http://www.idpf.org/2007/opf">
        {/*todo*/}
        </package>
    )
}
