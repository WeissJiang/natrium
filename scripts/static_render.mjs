#!/usr/bin/env node
import puppeteer from 'puppeteer'

console.time('render')
const browser = await puppeteer.launch()

async function render(url) {
    const page = await browser.newPage()
    await page.goto(url, { waitUntil: 'networkidle0' })
    return page.evaluate(() => document.documentElement.outerHTML)
}

const baseUrl = 'http://localhost:8080/'

const pageUrls = ['']

// render results
const results = await Promise.all(pageUrls.map(it => render(baseUrl + it)))

await browser.close()
console.timeEnd('render')
