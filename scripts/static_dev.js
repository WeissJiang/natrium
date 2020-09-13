#!/usr/bin/env node
const { readFile } = require('fs')
const { join: joinPath } = require('path')
const { createServer } = require('http')
const { render: renderLess } = require('less')
const { startService } = require('esbuild')
const express = require('express')

const staticPath = joinPath(__dirname, '..', 'web/static')
const resolveFilePath = (path) => joinPath(staticPath, path)

async function readFileAsString(filePath) {
    return new Promise((resolve, reject) => {
        readFile(filePath, { encoding: 'utf-8' }, (err, data) => {
            if (err) {
                reject(err)
            } else {
                resolve(data.toString('utf8'))
            }
        })
    })
}

/**
 * Transformer to transform javascript, less
 */
async function getTransformer() {
    const service = await startService()

    async function transformEsm(filePath) {
        const input = await readFileAsString(filePath)
        const { js, warnings } = await service.transform(input, {
            target: 'es2018',
            loader: 'jsx'
        })
        if (warnings.length) {
            console.warn(...warnings)
        }
        return js
    }

    async function transformCss(filePath) {
        const input = await readFileAsString(filePath)
        const output = await renderLess(input)
        return output.css
    }

    return async function (req, res, next) {
        // js module
        if (/.+\.(mjs|jsx|ts|tsx)$/.test(req.path)) {
            const transformed = await transformEsm(resolveFilePath(req.path))
            res.contentType('text/javascript')
            res.send(transformed)
        }
        // css module
        else if (/.+\.(less)$/.test(req.path)) {
            const transformed = await transformCss(resolveFilePath(req.path))
            const injection = `
            ; (function (encoded) {
                    var text = decodeURIComponent(encoded)
                    var style = document.createElement('style')
                    style.innerHTML = text
                    document.head.appendChild(style)
                })("${encodeURIComponent(transformed)}")
            `
            res.contentType('text/javascript')
            res.send(injection)
        }
        // others
        else {
            next()
        }
    }
}

async function main() {
    const app = express()
    app.use(await getTransformer())
    app.use(express.static(staticPath))
    // forward module from modules to node_modules if module absent
    app.use('/modules', express.static(joinPath(__dirname, '..', 'node_modules')))
    createServer(app).listen(3000, () => console.log('serving on 3000'))
}

if (require.main === module) {
    main().catch(err => console.error(err))
}

