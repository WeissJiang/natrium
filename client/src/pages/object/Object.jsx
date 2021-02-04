import React from 'react'

import 'antd/dist/antd.css'
import '@/styles/tailwind.css'

import { Button, message, Table, Typography } from 'antd'

const { Title } = Typography

export default function Object(props) {

    function handleClick() {
        message.info('Clicked')
    }

    return (
        <div className="flex flex-col justify-center items-center py-4">
            <div>
                <Title level="3">Object List</Title>
                <Table />
            </div>
        </div>
    )
}
