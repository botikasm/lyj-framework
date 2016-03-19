/* Tab Item */

var React = require('react');

module.exports = React.createClass({

    handleClick: function(e){
        e.preventDefault();
        this.props.handleClick();
    },

    render: function(){
        return (
            <li className={this.props.isCurrent ? 'current' : null}>
                <a onClick={this.handleClick} href={this.props.url}>
                    <label> {this.props.name}</label>
                </a>
            </li>
        );
    }
});