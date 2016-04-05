// ------------------------------------------------------------------------
//                      i m p o r t s
// ------------------------------------------------------------------------

var React = require('react');

var LikeButton = require('./components/test/likebutton');

// ------------------------------------------------------------------------
//                      i m p o r t s
// ------------------------------------------------------------------------
try {

    module.exports = React.createClass({

        getInitialState: function () {

            //input_handler.init();

            return {
                currentMenuItem: "",
                currentTabList: null
            };
        },

        changeMenuItem: function (title, attr) {

            this.replaceState(this.getInitialState());
            this.setState({currentMenuItem: title});
            this.setState({currentAttr: attr});
        },

        render: function () {

            return <div>
                
                <LikeButton />
                
            </div>;
        }
    });


} catch (err) {
    console.error('app.jsx', err);
}