import HttpProxy from 'http-proxy'

function proxyTo(target) {
    const proxy = HttpProxy.createServer({ target })
    return (req, res) => proxy.web(req, res)
}

const API_TYPE = {
    PROD_API: 'https://natrium.herokuapp.com/',
    LOCAL_API: 'http://localhost:8080/',
}

const API = API_TYPE.LOCAL_API

export default {
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
