import React, { useState } from 'react'

import 'antd/dist/antd.css'
import '@/styles/tailwind.css'

import { Button, message, Table, Typography } from 'antd'

const columns = [
    {
        title: 'Object key',
        dataIndex: 'objectKey',
        key: 'objectKey',
    },
    {
        title: 'Filename',
        dataIndex: 'filename',
        key: 'filename',
    },
    {
        title: 'URL',
        dataIndex: 'url',
        key: 'url',
    },
]

const mockObjectList = [
    { key: '1', objectKey: 'abc', filename: 'Filename1', url: 'https://' },
    { key: '2', objectKey: 'bcd', filename: 'Filename2', url: 'https://' },
    { key: '3', objectKey: 'cde', filename: 'Filename3', url: 'https://' },
]

export default function Object(props) {

    const [objectList, setObjectList] = useState(mockObjectList)

    return (
        <div className="flex flex-col justify-center items-center p-4">
            <Table className="w-full" columns={columns} dataSource={objectList} />
        </div>
    )
}
