const document = {
    getElementsByTagName() {
        return []
    },
    createElement() {
        return {
            appendChild() {
            }
        }
    },
    createTextNode() {
    },
    currentScript: '?',
    head: {
        appendChild() {
        },
        removeChild() {
        }
    }
}
const window = {
    location: {},
    document,
}
