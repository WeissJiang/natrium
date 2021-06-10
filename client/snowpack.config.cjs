const { createServer } = require('http-proxy')

function proxyTo(target) {
    const proxy = createServer({ target })
    return (req, res) => proxy.web(req, res)
}

const PROD_API = 'https://natrium.herokuapp.com/'
const LOCAL_API = 'http://localhost:8080/'

const API = LOCAL_API

module.exports = {
    mount: {
        './src': '/',
    },
    routes: [{ src: '/api/.*', dest: proxyTo(API) }],
    devOptions: {
        port: 8000,
        open: 'none',
    },
    buildOptions: {
        out: './dist',
    },
}
