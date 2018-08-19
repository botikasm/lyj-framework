/**
 * Sample link:
 * http://localhost:63342/_landing_bot_dashboard/index.html#!landing/signin
 */
import Page from "../../../../../../vendor/lyts_core/view/pages/page/Page";
import ElementWrapper from "../../../../../../vendor/lyts_core/view/components/ElementWrapper";
import {Route, Router} from "../../../../../../vendor/lyts_core/view/Router";
import view from "./view";
import globals from "../../../../../globals";
import ly from "../../../../../../vendor/lyts_core/ly";
import constants from "../../../../../constants";
import {Listener} from "../../../../../../vendor/lyts_core/commons/events/Events";
import AuthController from "../../../../../controllers/AuthController";
import ToastController from "../../../../../controllers/ToastController";
import ModelAccount from "../../../../../model/ModelAccount";
import console from "../../../../../../vendor/lyts_core/commons/console";
import ServiceController from "../../../../../controllers/ServiceController";
import errors from "../../../../../controllers/utils/errors";


export default class PageSignup
    extends Page {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private readonly ROUTE_MAIN = 'generic/';
    private readonly ROUTE_HOME = 'home/';
    private readonly ROUTE_SIGNIN = 'auth/signin';

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private _email: string;
    private _password: string;
    private _repassword: string;
    private _company_id: string;

    private readonly _fld_logo: ElementWrapper;
    private readonly _fld_email: ElementWrapper;
    private readonly _fld_password: ElementWrapper;
    private readonly _fld_repassword: ElementWrapper;

    private readonly _btn_goto_login: ElementWrapper;
    private readonly _btn_register: ElementWrapper;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    constructor(route: Route) {
        super(route);

        this._email = '';
        this._password = '';
        this._repassword = '';

        this._fld_logo = super.getFirst('#' + this.uid + '_fld_logo');
        this._fld_email = super.getFirst('#' + this.uid + '_fld_email');
        this._fld_password = super.getFirst('#' + this.uid + '_fld_password');
        this._fld_repassword = super.getFirst('#' + this.uid + '_fld_repassword');

        this._btn_goto_login = super.getFirst('#' + this.uid + '_btn_goto_login');
        this._btn_register = super.getFirst('#' + this.uid + '_btn_register');
    }

    // ------------------------------------------------------------------------
    //                      o v e r r i d e
    // ------------------------------------------------------------------------

    protected render(): string {
        return view(this.uid, {});
    }

    protected free(): void {
        this.removeListeners();
        console.log('PageSignup.free', this.uid);
    }

    protected ready(): void {
        this.init();
    }

    public show(): void {
        super.show();
        /*Animate.apply(AnimateEffect.fadeIn, this.element, () => {
            console.log('PageSignup.show', AnimateEffect.fadeIn + ' animation terminated');
        });*/
    }

    public hide(): void {
        super.hide();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private init(): void {
        try {
            this.getUrlParams();
            this.initData();
            this.initListeners();
            this.initView();
            this.setView();

            globals.Materialize.updateTextFields();
        } catch (err) {
            console.error("PageSignup.init", err);
        }
    }

    private getUrlParams(): void {
        try {
            // this._company_id = ly.browser.getParameterByName(constants.URL_PARAM_COMPANY_ID, '');
        } catch (err) {
            console.error("PageSignup.initCompanyId", err);
        }
    }

    private initData(): void {
        try {
            this._email = this._fld_email.value();
            this._password = this._fld_password.value();
            this._repassword = this._fld_repassword.value();
        } catch (err) {
            console.error("PageSignup.initValues", err);
        }
    }

    private initListeners(): void {
        try {
            this._fld_logo.addEventListener('click', ly.lang.funcDebounce(this,
                this.onFldLogoClick, constants.DEBOUNCE_TIME_MS, true));
            this._fld_email.addEventListener('keyup', this.onFldEmailUpdate);
            this._fld_email.addEventListener('change', this.onFldEmailUpdate);
            this._fld_password.addEventListener('keyup', this.onFldPasswordUpdate);
            this._fld_password.addEventListener('change', this.onFldPasswordUpdate);
            this._fld_repassword.addEventListener('keyup', this.onFldRepasswordUpdate);
            this._fld_repassword.addEventListener('change', this.onFldRepasswordUpdate);
            this._btn_goto_login.addEventListener('click', ly.lang.funcDebounce(this,
                this.onFldGotoLoginClick, constants.DEBOUNCE_TIME_MS, true))

            this._btn_register.addEventListener('click',
                ly.lang.funcDebounce(this, this.onBtnRegisterClick, constants.DEBOUNCE_TIME_MS, true));
        } catch (err) {
            console.error("PageSignup.initListeners", err);
        }
    }

    private removeListeners(): void {
        try {
            this._fld_logo.removeEventListener();
            this._fld_email.removeEventListener();
            this._fld_password.removeEventListener();
            this._fld_repassword.removeEventListener();

            this._btn_goto_login.removeEventListener();

            this._btn_register.removeEventListener();
        } catch (err) {
            console.error("PageSignup.removeListeners", err);
        }
    }

    // validation
    private hasEmail(): boolean {
        return !!this._email;
    }

    private hasPassword(): boolean {
        return !!this._password;
    }

    private hasRepassword(): boolean {
        return !!this._repassword;
    }

    private isValidEmail(): boolean {
        return ly.lang.isEmail(this._email);
    }

    private passwordAndRepasswordMatch(): boolean {
        return this._password === this._repassword;
    }

    private initView(): void {
        try {

        } catch (err) {
            console.error("PageSignup.initView", err);
        }
    }

    private setView(): void {
        try {
            // email
            this._fld_email.classRemove('invalid');
            if (this.hasEmail() && !this.isValidEmail()) {
                this._fld_email.classAdd('invalid');
            }

            // repassword
            this._fld_repassword.classRemove('invalid');
            if (this.hasPassword() && this.hasRepassword() && !this.passwordAndRepasswordMatch()) {
                this._fld_repassword.classAdd('invalid');
            }

        } catch (err) {
            console.error("PageSignup.setView", err);
        }
    }

    // events handler
    private onFldLogoClick(): void {
        try {
            // goto main
            Router.instance().goto(this.ROUTE_MAIN);
        } catch (err) {
            console.error("PageSignup.onBtnCompanyClick()", err)
        }
    }

    private onFldEmailUpdate(e: Event): void {
        try {
            if (!!e) {
                e.preventDefault();
            }
            this._email = this._fld_email.value() || '';
            this.setView();
        } catch (err) {
            console.error("PageSignup.onFldEmailUpdate", err);
        }
    }


    private onFldPasswordUpdate(e: Event): void {
        try {
            if (!!e) {
                e.preventDefault();
            }
            this._password = this._fld_password.value() || '';
            this.setView();
        } catch (err) {
            console.error("PageSignup.onFldPasswordUpdate", err);
        }
    }

    private onFldRepasswordUpdate(e: Event): void {
        try {
            if (!!e) {
                e.preventDefault();
            }
            this._repassword = this._fld_repassword.value() || '';
            this.setView();
        } catch (err) {
            console.error("PageSignup.onFldRepasswordUpdate", err);
        }
    }

    private onFldGotoLoginClick(e: Event): void {
        try {
            if (!!e) {
                e.preventDefault();
            }

            // goto register
            Router.instance().goto(this.ROUTE_SIGNIN);

        } catch (err) {
            console.error("PageSignup.onFldGotoLoginClick", err)
        }
    }

    private onBtnRegisterClick(e: Event): void {
        try {
            if (!!e) {
                e.preventDefault();
            }
            if (!this.hasEmail() || !this.hasPassword() || !this.hasRepassword()) {
                ToastController.instance().showError(ly.i18n.get('msg_email_and_password_required'));
            } else if (!this.isValidEmail()) {
                ToastController.instance().showError(ly.i18n.get('msg_email_not_valid'));
            } else if (!this.passwordAndRepasswordMatch()) {
                ToastController.instance().showError(ly.i18n.get('msg_password_and_repassword_not_match'));
            } else {

                this.signup((error, user) => {
                    if (!!error) {
                        ToastController.instance().showError(error);
                    } else if (!!user) {
                        Router.instance().goto(this.ROUTE_HOME);
                    }
                });


            }

        } catch (err) {
            console.error("PageSignup.onBtnRegisterClick", err);
        }
    }

    private signup(callback: Listener): void {

        if (!!this._email && !!this._password && !!this._repassword) {
            AuthController.instance().register(
                this._email,
                this._password,
                (error, user) => {
                    try {
                        if (!!error) {
                            console.error("PageSignup.register#api", error);
                            ly.lang.funcInvoke(callback, error, false);
                        } else {
                            ly.lang.funcInvoke(callback, false, user);
                        }
                    } catch (err) {
                        console.error("PageSignup.register", err);
                        ly.lang.funcInvoke(callback, err, false);
                    }
                });
        }
    }


}