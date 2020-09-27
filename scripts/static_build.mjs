#!/usr/bin/env node
import { fileURLToPath } from 'url'
import { join as joinPath, dirname } from 'path'
import { readdir, stat, mkdir, copyFile, writeFile } from 'fs/promises'

import { transformCss, transformEsm, readFileAsString } from './transforming.mjs'

const __dirname = dirname(fileURLToPath(import.meta.url))

const staticPath = joinPath(__dirname, '..', 'web/static')
const nodeModulesPath = joinPath(__dirname, '..', 'node_modules')
const distPath = joinPath(__dirname, '..', 'web/dist')

async function compileFile(srcFilePath, destFilePath) {
    // js module
    if (/.+\.(mjs|jsx|ts|tsx)$/.test(srcFilePath)) {
        const transformed = await transformEsm(srcFilePath)
        await writeFile(destFilePath, transformed)
    }
    // css module
    else if (/.+\.(less)$/.test(srcFilePath)) {
        const transformed = await transformCss(srcFilePath)
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

function getAllValues(object) {
    const v = []
    const rg = o => Object.values(o).forEach(it => typeof it === 'object' ? rg(it) : v.push(it))
    rg(object)
    return v
}

async function copyDeps() {
    const json = await readFileAsString(joinPath(staticPath, 'deps.json'))
    const deps = getAllValues(JSON.parse(json))
    if (!deps.length) {
        return
    }
    for (const dep of deps) {
        const r = /^\/modules\//
        if (!r.test(dep)) {
            continue
        }
        const path = dep.replace(r, '')
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
    transformEsm.service.stop()
}

main().catch(err => console.error(err))
