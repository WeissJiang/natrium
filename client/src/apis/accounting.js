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

export async function createMonthData(token, monthData) {
    const response = await fetch('/api/accounting/monthData', {
        method: 'POST',
        headers: {
            'X-Token': token,
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(monthData),
    })
    const result = await response.json()
    if (result.error) {
        throw new Error(result.error)
    }
    return result.payload
}

export async function updateMonthData(token, monthData) {
    const response = await fetch('/api/accounting/monthData', {
        method: 'PUT',
        headers: {
            'X-Token': token,
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(monthData),

    })
    const result = await response.json()
    if (result.error) {
        throw new Error(result.error)
    }
    return result.payload
}