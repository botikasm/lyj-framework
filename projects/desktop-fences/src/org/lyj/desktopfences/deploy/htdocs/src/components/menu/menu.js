/* Menu */

var React = require('react');

var style = require('../styles/style');


module.exports = React.createClass({

    render: function() {
        return (
            <div className="menu" style={style.getMenuStyle()}>
                <div className="menu-title">
                    <h2>
                        <b>Fontanot</b>
                    </h2>
                </div>
                <div>{this.props.children}</div>
            </div>
        );
    }
});