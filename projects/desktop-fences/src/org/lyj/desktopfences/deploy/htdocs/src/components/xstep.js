/* XStep */


/* imports */

var React = require('react');
var $ = require('jquery');

// GUI
var Menu = require('../components/menu/menu');
var MenuItem = require('../components/menu/menu-item');
var WorkSpace = require('../components/workspace/workspace');

// api
var ApiUtils = require('../api/ApiUtils');

var api_utils = new ApiUtils();
var app_version = "";



//Main XStep Structure

module.exports = React.createClass({

    getInitialState: function () {

        return {
            currentMenuItem:"",
            currentTabList: null
        };
    },

    changeMenuItem: function(title,attr) {
        this.replaceState(this.getInitialState());
        this.setState({currentMenuItem: title});
        this.setState({currentAttr: attr});
    },

    render: function () {

        return <div>
            <Menu>
                <MenuItem hash="home" action={this.changeMenuItem}>Home</MenuItem>
                <MenuItem hash="customers" attr={customerAttr} action={this.changeMenuItem}> Clienti </MenuItem>
                <MenuItem hash="quotations" attr={quotationAttr} action={this.changeMenuItem}> Preventivi </MenuItem>
            </Menu>

            {this.state.currentMenuItem != "" ? <WorkSpace key={this.state.currentMenuItem}topBarName={this.state.currentMenuItem} attr={this.state.currentAttr}/> : null}

        </div>;
    }
});


/* Menu Items model */

// Customer
var customerAttr=[
    {
        'tabs':[
            { 'id': 1,
                'name': 'Dati Cliente',
                'url': '/data-customer',
                'content':[
                    {
                        'form':[
                            { 'tag': 'input', 'name': 'customer-name', 'type': 'text', label:'Nome' },
                            { 'tag': 'input', 'name': 'customer-surname', 'type': 'text', label:'Cognome' },
                            { 'tag': 'input', 'name': 'customer-cf', 'type': 'text', label:'Codice Fiscale/P.IVA' },
                            { 'tag': 'input', 'name': 'customer-company', 'type': 'text', label:'Società' }
                        ]
                    }
                ]
            },
            {  'id': 2,
                'name': 'Dati Referente',
                'url': '/data-ref',
                'content':[]
            },
            { 'id': 3, 'name': 'Preventivi', 'url': '/quotations','content':[] }
        ]
    }
];


// Quotation
var quotationAttr=[
    {
        'tabs':[
            {
                'id': 4,
                'name': 'Dati Preventivo',
                'url': '/data-customer',
                'content':[
                    {
                        'form':[
                            { 'tag': 'input', 'name': 'customer-name', 'type': 'text', label:'Preventivo' },
                            { 'tag': 'input', 'name': 'customer-surname', 'type': 'text', label:'Colore' },
                            { 'tag': 'input', 'name': 'customer-cf', 'type': 'text', label:'Test' },
                            { 'tag': 'input', 'name': 'customer-cf', 'type': 'text', label:'Test' },
                            { 'tag': 'input', 'name': 'customer-cf', 'type': 'text', label:'Test' },
                            { 'tag': 'input', 'name': 'customer-cf', 'type': 'text', label:'Test' },
                            { 'tag': 'input', 'name': 'customer-cf', 'type': 'text', label:'Test' },
                            { 'tag': 'input', 'name': 'customer-cf', 'type': 'text', label:'Test' },
                            { 'tag': 'input', 'name': 'customer-cf', 'type': 'text', label:'Test' },
                            { 'tag': 'input', 'name': 'customer-cf', 'type': 'text', label:'Test' },
                            { 'tag': 'input', 'name': 'customer-cf', 'type': 'text', label:'Test' },
                            { 'tag': 'input', 'name': 'customer-cf', 'type': 'text', label:'Test' },
                            { 'tag': 'input', 'name': 'customer-cf', 'type': 'text', label:'Test' },
                            { 'tag': 'input', 'name': 'customer-company', 'type': 'file', label:'Seleziona immagine' }
                        ]
                    }
                ]
            },
            { 'id': 5,
                'name': 'Dettagli',
                'url': '/data-ref',
                'content':[
                    {
                        'form':[
                            { 'tag': 'input', 'name': 'customer-name', 'type': 'text', label:'Dettagli' },
                            { 'tag': 'input', 'name': 'customer-surname', 'type': 'text', label:'Misure' },
                            { 'tag': 'input', 'name': 'customer-cf', 'type': 'text', label:'Test' },
                            { 'tag': 'input', 'name': 'customer-company', 'type': 'text', label:'Essenza' }
                        ]
                    }
                ]
            }
        ]
    }
];