// ------------------------------------------------------------------------
//                      i m p o r t s
// ------------------------------------------------------------------------

var React = require('react');
var Materialize = require('materialize');

// ------------------------------------------------------------------------
//                      e x p o r t s
// ------------------------------------------------------------------------
try {

    var btn_style = {
        bottom: '45px',
        right: '24px'
    };
    
    module.exports = React.createClass({
        getInitialState: function () {
            return {liked: false};
        },
        handleClick: function (event) {
            this.setState({liked: !this.state.liked});
        },
        render: function () {
            var text = this.state.liked ? 'YES' : 'NO';
            return (
                
                <div className="fixed-action-btn" style={btn_style}>
                    <a className="btn-floating btn-large waves-effect waves-circle waves-light">
                        <i className="large material-icons">mode_edit</i>
                    </a>
                    <ul>
                        <li><a className="btn-floating red" onClick={this.handleClick}><i className="material-icons">insert_chart</i></a></li>
                        <li><a className="btn-floating yellow darken-1"><i className="material-icons">format_quote</i></a></li>
                        <li><a className="btn-floating green"><i className="material-icons">publish</i></a></li>
                        <li><a className="btn-floating blue"><i className="material-icons">attach_file</i></a></li>
                    </ul>
                </div>
            );
        }
    });

} catch (err) {
    console.error('likebutton.jsx', err);
}