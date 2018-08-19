const IS_LOCALE: boolean = true;
const HOST_LOCALE: string = 'https://localhost:4000';
const HOST: string = 'https://api.conversacon.com:4000';

const UID: string = 'hello_world';
const VERSION: string = '1.0.0';

const constants = {

    uid: UID,
    version: VERSION,

    //-- host --//
    //-- host --//
    host: IS_LOCALE ? HOST_LOCALE : HOST,

    // APP IDENTIFIER
    APP_TOKEN: "iuhdiu87w23ruh897dfyc2w3r", // test token with limited access

    DEBOUNCE_TIME_MS: 1000,
    DELAY_TIME_MS: 400,

    //-- STANDARD COMPONENTS EVENTS --//
    EVENT_ON_CLICK: "on_click",
    EVENT_ON_LOGIN: "on_login",
    EVENT_ON_LOGOUT: "on_logout",

};

export default constants;