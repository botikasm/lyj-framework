
/*Menu Item*/

var React = require('react');
var Style = require('../styles/style');
var WorkSpace = require('../workspace/workspace');



module.exports = React.createClass({

    handleClick: function(title, attr){
        this.props.action(title,attr);
    },

    render: function() {

        var children = "";

        //Detect user selection for home
        if(null != this.props.children && this.props.hash != "home")
            children = this.props.children;
        else
            children = "";


        return (
            <div className="menu-link" style={Style.getMenuItemStyle()} onClick={this.handleClick.bind(this,children,this.props.attr)}>{this.props.children}</div>
        );
    }
});


