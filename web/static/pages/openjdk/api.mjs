import { assertEquals } from '/modules/assertions.mjs'

// See https://api.adoptopenjdk.net/

export async function getOpenJDKInfo2(params) {
    const searchParams = new URLSearchParams(params)
    const response = await fetch(`https://api.adoptopenjdk.net/v2/info/releases/openjdk${params.version}?${searchParams}`)
    assertEquals(response.status, 200, `response is ${response.status}`)
    return await response.json()
}