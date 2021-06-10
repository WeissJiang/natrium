export async function getMonthData(token, month) {
    const response = await fetch(`/api/accounting/monthData?month=${month}`, {
        method: 'GET',
        headers: { 'X-Token': token },
    })
    const result = await response.json()
    if (result.error) {
        throw new Error(result.error)
    }
    return result.payload
}