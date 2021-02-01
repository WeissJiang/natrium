module.exports = {
    mount: {
        './src': '/',
    },
    plugins: [
        'snowpack-plugin-less',
    ],
    alias: {
        '@': './src',
    },
    routes: [
        { match: 'routes', src: '.*', dest: '/' }
    ],
    packageOptions: {},
    devOptions: {},
    buildOptions: {
        out: './dist',
    },
}
