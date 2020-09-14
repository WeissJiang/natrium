#!/usr/bin/env node
import { fileURLToPath } from 'url'
import { join as joinPath, dirname, basename } from 'path'
import { readFile, readdir, stat, mkdir, copyFile, writeFile } from 'fs/promises'
import build from 'esbuild'
import less from 'less'

const __dirname = dirname(fileURLToPath(import.meta.url))

const staticPath = joinPath(__dirname, '..', 'web/static')
const nodeModulesPath = joinPath(__dirname, '..', 'node_modules')
const distPath = joinPath(__dirname, '..', 'web/dist')

async function readFileAsString(filePath) {
    const data = await readFile(filePath, { encoding: 'utf8' })
    return data.toString('utf8')
}

async function transformCss(filePath) {
    const input = await readFileAsString(filePath)
    const output = await less.render(input)
    return output.css
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

const deps = []

function collectDeps(text) {
    const r = /\s*import\s+("(.+?)"|'(.+?)')\s*/g
    let a
    while (a = r.exec(text)) {
        deps.push(a[2] || a[3])
    }
}

async function compileFile(srcFilePath, destFilePath) {
    // js module
    if (/.+\.(mjs|jsx|ts|tsx)$/.test(srcFilePath)) {
        const transformed = await transformEsm(srcFilePath)
        if (/deps.*.mjs/.test(basename(srcFilePath))) {
            collectDeps(transformed)
        }
        await writeFile(destFilePath, transformed)
    }
    // css module
    else if (/.+\.(less)$/.test(srcFilePath)) {
        const transformed = await transformCss(srcFilePath)
        const injection = `
        ;(function (encoded) {
                var text = decodeURIComponent(encoded);
                var style = document.createElement('style');
                style.innerHTML = text;
                document.head.appendChild(style);
            })("${encodeURIComponent(transformed)}");
        `
        await writeFile(destFilePath, injection.replace(/\s+/g, ' ').trim())
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

async function copyDeps() {
    if (!deps.length) {
        return
    }
    for (const dep of deps) {
        const path = dep.replace(/^\/modules\//, '')
        const srcPath = joinPath(nodeModulesPath, path)
        const destPath = joinPath(distPath, 'modules', path)
        await mkdir(dirname(destPath), { recursive: true })
        await copyFile(srcPath, destPath)
    }

}

async function main() {
    await mkdir(distPath, { recursive: true })
    await traverseDir(staticPath, distPath)
    // copy modules if required
    await copyDeps()
    service.stop()
}

main().catch(err => console.error(err))
