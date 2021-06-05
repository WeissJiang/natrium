const { createServer } = require('http-proxy')

function proxyTo(target) {
    const proxy = createServer({ target })
    return (req, res) => proxy.web(req, res)
}

const API = 'https://natrium.herokuapp.com/'

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
