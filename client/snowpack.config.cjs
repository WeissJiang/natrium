module.exports = {
    mount: {
        './src': '/',
    },
    plugins: [
        'snowpack-plugin-less',
        ['@snowpack/plugin-postcss', { config: './postcss.config.cjs' }],
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
