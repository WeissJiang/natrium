import '/webjars/react/16.13.1/umd/react.development.js'
import '/webjars/react-dom/16.13.1/umd/react-dom.development.js'

function Nano() {
    return (React.createElement("div", { className: "container" },
        React.createElement("div", { className: "title" }, "nano-bot")));
}


ReactDOM.render(React.createElement(Nano, null), document.getElementById('app'));
