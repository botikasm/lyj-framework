var React = require('react');

module.exports = React.createClass({

    render: function() {

        return <div className="topbar">
            <h3>{this.props.name}</h3>
            <div className="topbar-buttons">
                <button type="button">Edit</button>
                <button type="button">Add</button>
                <button type="button" disabled>Delete</button>
            </div>
        </div>
    }
});