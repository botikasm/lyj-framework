/* Style */


var React = require('react');



module.exports = {

    getMenuItemStyle: function () {
        return{
            padding: "0.6em 0 0.6em 2em",
            textDecoration:"none",
            width:"100%"
        }
    },

    getMenuStyle:function(){

        return {
            color:"#ffffff",
            top:"0",
            bottom:"0",
            left:"0",
            overflow:"hidden",
            position:"absolute",
            zIndex:"1000",
            margin: "0",
            width: "25%",
            backgroundColor:"#03A9F4"
        }
    },

    getWorkspaceStyle:function(){

        return {
            color:"#ffffff",
            height:"auto",
            width: "75%",
            right:"0",
            position:"absolute",
            backgroundRepeat:"no-repeat"
           // backgroundImage:"url(img/fontanot_contract.jpg)"
        }
    }
};
