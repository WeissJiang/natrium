#!/usr/bin/env node
import { fileURLToPath } from 'url'
import { dirname, join as joinPath } from 'path'
import { readdir, stat, unlink, rmdir } from 'fs/promises'

const __dirname = dirname(fileURLToPath(import.meta.url))

const distPath = joinPath(__dirname, '..', 'web/dist')

async function removeDir(dirPath) {
    const files = await readdir(dirPath)
    for (const file of files) {
        const filePath = joinPath(dirPath, file)
        const fileStats = await stat(filePath)
        if (fileStats.isDirectory()) {
            await removeDir(filePath)
        } else {
            await unlink(filePath)
        }
    }
    await rmdir(dirPath)
}

async function main() {
    await removeDir(distPath)
}

main().catch(err => console.error(err))
