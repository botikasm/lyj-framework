//-- static --//
import lang from "./commons/lang";
import format from "./commons/format";
import strings from "./commons/strings";
import objects from "./commons/objects";
import random from "./commons/random";
import browser from "./view/browser";
import installer from "./view/installer";
import cookies from "./view/cookies";
import dom from "./view/dom";
import i18n from "./view/i18n";
//-- classes --//
import {Dictionary} from "./commons/collections/Dictionary";
import Events from "./commons/events/Events";
import EventEmitter from "./commons/events/EventEmitter";
import {HttpClient} from "./net/HttpClient";
//-- views --//
import Component from "./view/components/Component";
//-- singleton --//
import Application from "./application/Application";

const root: any = window;

// ------------------------------------------------------------------------
//                      l y
// ------------------------------------------------------------------------

const ly = {

    window: root,

    lang: lang,
    format: format,
    strings: strings,
    objects: objects,
    random: random,
    browser: browser,
    installer: installer,
    cookies: cookies,
    dom: dom,
    i18n: i18n,

    Events: Events,
    EventEmitter: EventEmitter,
    Dictionary: Dictionary,
    HttpClient: HttpClient,

    //-- v i e w --//
    Component: Component,

    //-- s i n g l e t o n --//
    Application: Application

};


// ------------------------------------------------------------------------
//                      e x p o r t s
// ------------------------------------------------------------------------

export default ly;