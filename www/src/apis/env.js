const NANO_API_BASE = import.meta.env['SNOWPACK_PUBLIC_NANO_API_BASE']

export function withNanoApi(endpoint) {
    if (!NANO_API_BASE) {
        return endpoint
    }
    return new URL(endpoint, NANO_API_BASE).toString()
}
