(async function less2css(style) {
    const transformed = await less.render(style)
    return `
    ; (function (encoded) {
        var text = decodeURIComponent(encoded)
        var style = document.createElement('style')
        style.innerHTML = text
        document.head.appendChild(style)
    })("${encodeURIComponent(transformed.css)}")`
})
