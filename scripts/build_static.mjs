#!/usr/bin/env node
import { fileURLToPath } from 'url'
import { dirname, join as joinPath } from 'path'
import { copyFile, mkdir, readdir, stat, writeFile } from 'fs/promises'

import { readFileAsString, transformCss, transformEsm } from './transforming.mjs'

const __dirname = dirname(fileURLToPath(import.meta.url))

const staticPath = joinPath(__dirname, '..', 'web/static')
const nodeModulesPath = joinPath(__dirname, '..', 'node_modules')
const distPath = joinPath(__dirname, '..', 'web/dist')

async function collectPaths(staticPath, distPath) {

    const destDirs = []
    const files = []

    async function recurseCollect(srcPath, destPath) {
        const currentFiles = await readdir(srcPath)
        for (const filename of currentFiles) {
            const srcFilePath = joinPath(srcPath, filename)
            const destFilePath = joinPath(destPath, filename)
            const fileStats = await stat(srcFilePath)
            if (fileStats.isDirectory()) {
                destDirs.push(destFilePath)
                await recurseCollect(srcFilePath, destFilePath)
            } else if (fileStats.isFile()) {
                files.push({ srcFilePath, destFilePath, filename })
            } else {
                throw new Error('Unexpected file type')
            }
        }
    }

    await recurseCollect(staticPath, distPath)

    return { destDirs, files }
}

async function compileAndCopyFiles(files) {

    async function compile(filename, filePath) {
        // js module
        if (/.+\.(mjs|jsx|ts|tsx)$/.test(filename)) {
            return await transformEsm(filePath)
        }
        // css module
        else if (/.+\.(less)$/.test(filename)) {
            return await transformCss(filePath)
        }
        // others
        else {
        }
    }

    for (const { srcFilePath, destFilePath, filename } of files) {
        const transformed = await compile(filename, srcFilePath)
        if (transformed) {
            await writeFile(destFilePath, transformed)
        } else {
            await copyFile(srcFilePath, destFilePath)
        }
    }
}

async function copyModules() {
    const modulesJson = await readFileAsString(joinPath(staticPath, 'modules.json'))
    const regex = /^\/modules\//

    function getModulePaths(jsonObject) {
        const modulePaths = []

        function search(item) {
            for (const it of Object.values(item)) {
                if (typeof it === 'object') {
                    search(it)
                } else if (regex.test(it)) {
                    modulePaths.push(it)
                }
            }
        }

        search(jsonObject)
        return modulePaths
    }

    const modules = getModulePaths(JSON.parse(modulesJson))

    if (!modules.length) {
        return
    }
    for (const module of modules) {
        const path = module.replace(regex, '')
        const srcPath = joinPath(nodeModulesPath, path)
        const destPath = joinPath(distPath, 'modules', path)
        await mkdir(dirname(destPath), { recursive: true })
        await copyFile(srcPath, destPath)
    }
}

async function createDirs(dirs) {
    for (const dir of dirs) {
        await mkdir(dir, { recursive: true })
    }
}

async function main() {
    // create dist dir
    await mkdir(distPath, { recursive: true })
    // collect dir and file paths
    const { destDirs, files } = await collectPaths(staticPath, distPath)
    // create dest dirs
    await createDirs(destDirs)
    // compile and copy files
    await compileAndCopyFiles(files)
    // copy modules if required
    await copyModules()
    transformEsm.service.stop()
}

main().catch(err => console.error(err))
