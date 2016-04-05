// ------------------------------------------------------------------------
//                      b o o t s t r a p p e r
// ------------------------------------------------------------------------

try{
    var React = require('react');
    var ReactDOM = require('react-dom');
    var $ = require('jQuery');
    var App = require('./app');
    
    
    ReactDOM.render(
        <App />,
        $('#app')[0]
    );
} catch (err) {
     console.error('index.jsx', err);
}

