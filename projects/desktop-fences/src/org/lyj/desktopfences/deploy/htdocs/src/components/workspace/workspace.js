/* WorkSpace */

var React = require('react');
var Style = require('../styles/style');
var TopBar = require('./topbar');
var Tabs = require('./tabs/tabs');
var Content = require('./tabs/tab-content');

module.exports = React.createClass({

    getInitialState: function () {

        return {
            currentTab: 0,
            currentContent:null
        };
    },




    changeTab: function(tab) {
        this.setState({ currentTab: tab.id });
        this.setState({ currentContent: tab.content});
    },

    render: function () {

        //console.log(this.props.attr.tabs);

        return (
            <div style={Style.getWorkspaceStyle()}>

                <TopBar name={this.props.topBarName}/>

                {this.props.attr.map(function (attr) {
                    return (
                        <Tabs currentTab={this.state.currentTab} tabList={attr.tabs} changeTab={this.changeTab}/>
                    );
                }.bind(this))}

                {null != this.state.currentContent ? <Content content={this.state.currentContent}/> : null}

            </div>
        )
    }
});

