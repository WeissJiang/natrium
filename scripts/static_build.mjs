#!/usr/bin/env node
import { fileURLToPath } from 'url'
import { join as joinPath, dirname } from 'path'
import { readFile } from 'fs'
import { createServer } from 'http'
import build from 'esbuild'
import less from 'less'

const __dirname = dirname(fileURLToPath(import.meta.url))

const staticPath = joinPath(__dirname, '..', 'web/static')
