module.exports = {
    mount: {
        '../web/static': '/',
    },
    plugins: [
        'snowpack-plugin-less',
    ],
    alias: {
        '@': '../web/static',
    },
    routes: [
        { match: 'routes', src: '.*', dest: '/' }
    ],
    packageOptions: {},
    devOptions: {},
    buildOptions: {
        out: '../web/dist',
    },
}
