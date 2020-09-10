(function transpileModule(script) {
    return ts.transpileModule(script, {
        compilerOptions: {
            lib: ['es2018', 'dom'],
            allowJs: true,
            target: 'es2018',
            jsx: ts.JsxEmit.React,
        }
    })
})
