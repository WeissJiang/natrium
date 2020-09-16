const localStorageExists = typeof localStorage !== 'undefined'

export function getLocalItem(key) {
    if (localStorageExists) {
        return localStorage.getItem(key)
    }
}

export function setLocalItem(key, value) {
    if (localStorageExists) {
        localStorage.setItem(key, value)
    }
}

export function removeLocalItem(key) {
    if (localStorageExists) {
        localStorage.removeItem(key)
    }
}

export default {
    getLocalItem,
    setLocalItem,
    removeLocalItem,
}
