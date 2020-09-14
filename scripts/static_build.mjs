#!/usr/bin/env node
import { fileURLToPath } from 'url'
import { join as joinPath, dirname } from 'path'
import { readFile, readdir, stat, mkdir, copyFile, writeFile } from 'fs/promises'
import build from 'esbuild'
import less from 'less'

const __dirname = dirname(fileURLToPath(import.meta.url))

const staticPath = joinPath(__dirname, '..', 'web/static')
const distPath = joinPath(__dirname, '..', 'web/dist')

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

async function compileFile(srcFilePath, destFilePath) {
    // js module
    if (/.+\.(mjs|jsx|ts|tsx)$/.test(srcFilePath)) {
        const transformed = await transformEsm(srcFilePath)
        await writeFile(destFilePath, transformed)
    }
    // css module
    else if (/.+\.(less)$/.test(srcFilePath)) {
        const transformed = await transformCss(srcFilePath)
        const injection = `
        ; (function (encoded) {
                var text = decodeURIComponent(encoded)
                var style = document.createElement('style')
                style.innerHTML = text
                document.head.appendChild(style)
            })("${encodeURIComponent(transformed)}")
        `
        await writeFile(destFilePath, transformed)
    }
    // others
    else {
        await copyFile(srcFilePath, destFilePath)
    }


}

async function traverseDir(srcPath, destPath) {
    const files = await readdir(srcPath)
    for (const file of files) {
        const srcFilePath = joinPath(srcPath, file)
        const destFilePath = joinPath(destPath, file)
        const fileStats = await stat(srcFilePath)
        if (fileStats.isDirectory()) {
            await mkdir(destFilePath, { recursive: true })
            await traverseDir(srcFilePath, destFilePath)
        } else if (fileStats.isFile()) {
            await compileFile(srcFilePath, destFilePath)
        } else {
            throw new Error('Unexpected file type')
        }
    }
}

async function main() {
    await mkdir(distPath, { recursive: true })
    await traverseDir(staticPath, distPath)
    service.stop()
    console.log('Static files built')
}

main().catch(err => console.error(err))