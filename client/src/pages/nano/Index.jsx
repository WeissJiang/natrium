import React from 'react'

import '../../styles/utilities.css'

export default function Index() {

    const backgroundStyle = {
        backgroundImage: 'url(https://source.unsplash.com/random/1600x900)',
        backgroundPosition: 'center',
        backgroundSize: 'cover',
    }

    return (
        <div style={backgroundStyle} className="relative flex flex-col justify-center items-center h-screen">
            <div style={{ textShadow: '1px 1px 3px rgb(36 37 47 / 25%)' }}
                 className="relative text-7xl bottom-20 text-white">nano
            </div>
        </div>
    );
}
