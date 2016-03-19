/* Tabs */

var React = require('react');
var Tab = require('./tab-item');
var Content = require('./tab-content');

module.exports = React.createClass({



    handleClick: function(tab){
        this.props.changeTab(tab);
    },

    render: function(){

        return (
            <div>
                {null != this.props.tabList ?
                <nav className="tabs">
                    <ul>
                        {this.props.tabList.map(function (tab) {
                            return <Tab handleClick={this.handleClick.bind(this, tab)} url={tab.url} name={tab.name} key={tab.id} isCurrent={(this.props.currentTab === tab.id)}/>
                        }.bind(this))}
                    </ul>
                </nav>
                    :""}
            </div>
        );
    }
});

