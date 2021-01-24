import https from 'https'

const req = https.request({
  host: 'nano-bot.herokuapp.com',
  port: '443',
  path: '/api/telegram/setWebhook',
  method: 'post',
  headers: {
    'X-Token': '',
  }
}, res => {
  let buffer = ''
  res.on('data', (chunk) => buffer += chunk)
  res.on('end', () => console.log(buffer))
  res.on('error', (error) => console.error(error))
})

req.on('error', (error) => console.error(error))

req.end()
