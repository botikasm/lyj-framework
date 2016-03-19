/* Tab Content */

var React = require('react');
var Form = require('../../form/form');

module.exports = React.createClass({

    render: function(){
        return(
            <div className="content">
                {null != this.props.content ?
                    <div>
                    {this.props.content.map(function (content) {
                        return <Form items={content.form}/>
                    }.bind(this))}
                        </div>
                :""}

            </div>
        );
    }
});

