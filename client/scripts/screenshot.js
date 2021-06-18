import puppeteer from 'puppeteer'

const url = process.argv[2]

const browser = await puppeteer.launch({ args: ['--no-sandbox'] })
const page = await browser.newPage()
await page.goto(url)
const screenshot = await page.screenshot({ type: 'png', fullPage: true })

process.stdout.write(screenshot, async (err) => {
    await browser.close()
})

