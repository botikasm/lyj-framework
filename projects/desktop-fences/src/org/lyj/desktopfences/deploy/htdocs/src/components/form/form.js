var React = require('react');

module.exports = React.createClass({

    render: function(){
        return (
            <form className="pure-form pure-form-stacked">
                <fieldset>
                {"" != this.props.items ?
                    <div className="pure-g">
                        {this.props.items.map(function (item) {
                            return (
                                <div className="pure-u-1-4">
                                    <label>{item.label}</label>
                                    <item.tag name={item.name} type={item.type}/>
                                </div>
                            );
                        })}
                    </div>
                    :""}
                </fieldset>
            </form>
        );
    }
});