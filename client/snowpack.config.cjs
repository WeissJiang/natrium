const { createServer } = require('http-proxy')

function proxyTo(target) {
    const proxy = createServer({ target })
    return (req, res) => proxy.web(req, res)
}

module.exports = {
    mount: {
        './src': '/',
    },
    routes: [
        { src: '/api/.*', dest: proxyTo('http://localhost:8080'), },
    ],
    devOptions: {
        port: 8000,
        open: 'none',
    },
    buildOptions: {
        out: './dist',
    },
}
