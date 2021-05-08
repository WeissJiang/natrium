const { createServer } = require('http-proxy')

function proxyTo(target) {
    const proxy = createServer({ target })
    return (req, res) => proxy.web(req, res)
}

module.exports = {
    mount: {
        './src': '/',
    },
    plugins: [],
    alias: {
        '@': './src',
    },
    routes: [
        { src: '/api/.*', dest: proxyTo('http://localhost:8080'), },
    ],
    packageOptions: {},
    devOptions: {
        port: 8000,
        open: 'none',
    },
    buildOptions: {
        out: './dist',
    },
}
