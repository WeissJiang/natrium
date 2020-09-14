#!/usr/bin/env node
import { fileURLToPath } from 'url'
import { join as joinPath, dirname } from 'path'
import { readFile } from 'fs/promises'
import { createServer } from 'http'
import build from 'esbuild'
import less from 'less'
import express from 'express'

const __dirname = dirname(fileURLToPath(import.meta.url))

const staticPath = joinPath(__dirname, '..', 'web/static')
const nodeModulesPath = joinPath(__dirname, '..', 'node_modules')
const resolveFilePath = (path) => joinPath(staticPath, path)

async function readFileAsString(filePath) {
    const data = await readFile(filePath, { encoding: 'utf8' })
    return data.toString('utf8')
}

const service = await build.startService()

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
    const output = await less.render(input)
    return output.css
}

/**
 *  transform javascript, less
 */
async function transform(req, res, next) {
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
        ;(function (encoded) {
                var text = decodeURIComponent(encoded);
                var style = document.createElement('style');
                style.innerHTML = text;
                document.head.appendChild(style);
            })("${encodeURIComponent(transformed)}");
            `
        res.contentType('text/javascript')
        res.send(injection.replace(/\s+/g, ' ').trim())
    }
    // others
    else {
        next()
    }
}

async function main() {
    const app = express()
    app.use(transform)
    app.use(express.static(staticPath))
    // forward module from modules to node_modules if module absent
    app.use('/modules', express.static(nodeModulesPath))
    createServer(app).listen(3000, () => console.log('serving on 3000'))
}

main().catch(err => console.error(err))

