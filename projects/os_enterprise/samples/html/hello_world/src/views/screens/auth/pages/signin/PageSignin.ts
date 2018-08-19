/**
 * Sample link:
 * http://localhost:63342/_landing_bot_dashboard/index.html#!company_auth/signin
 */
import ElementWrapper from "../../../../../../vendor/lyts_core/view/components/ElementWrapper";
import {Route, Router} from "../../../../../../vendor/lyts_core/view/Router";
import ly from "../../../../../../vendor/lyts_core/ly";
import constants from "../../../../../constants";
import {Listener} from "../../../../../../vendor/lyts_core/commons/events/Events";
import globals from "../../../../../globals";
import view from "./view";
import console from "../../../../../../vendor/lyts_core/commons/console";
import Page from "../../../../../../vendor/lyts_core/view/pages/page/Page";
import ToastController from "../../../../../controllers/ToastController";
import AuthController from "../../../../../controllers/AuthController";


export default class PageSignin
    extends Page {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private readonly ROUTE_MAIN = 'generic/main';
    private readonly ROUTE_HOME = 'home/';
    private readonly ROUTE_SIGNUP = 'auth/signup';
    private readonly ROUTE_RESET_PASSWORD = 'auth/reset_password';

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------
    private _email: string;
    private _password: string;

    private readonly _fld_logo: ElementWrapper;
    private readonly _fld_email: ElementWrapper;
    private readonly _fld_password: ElementWrapper;
    private readonly _btn_forgot_password: ElementWrapper;
    private readonly _btn_register: ElementWrapper;
    private readonly _btn_signin: ElementWrapper;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    constructor(route: Route) {
        super(route);

        this._email = '';
        this._password = '';

        this._fld_logo = super.getFirst('#' + this.uid + '_fld_logo');
        this._fld_email = super.getFirst('#' + this.uid + '_fld_email');
        this._fld_password = super.getFirst('#' + this.uid + '_fld_password');
        this._btn_forgot_password = super.getFirst('#' + this.uid + '_btn_forgot_password');
        this._btn_register = super.getFirst('#' + this.uid + '_btn_register');
        this._btn_signin = super.getFirst('#' + this.uid + '_btn_signin');

    }

    // ------------------------------------------------------------------------
    //                      o v e r r i d e
    // ------------------------------------------------------------------------

    protected render(): string {
        return view(this.uid, {});
    }

    protected free(): void {

        // release memory
        this.removeListeners();
        console.log('PageSignin.free', this.uid);
    }

    protected ready(): void {
        this.init();
    }

    public show(): void {
        super.show();

        /*Animate.apply(AnimateEffect.fadeIn, this.element, () => {
            console.log('PageSignin.show', AnimateEffect.fadeIn + ' animation terminated');
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
            this.initData();
            this.initListeners();
            this.setView();

            globals.Materialize.updateTextFields();
        } catch (err) {
            console.error("PageSignin.init", err);
        }
    }

    private initData(): void {
        try {
            this._email = this._fld_email.value();
            this._password = this._fld_password.value();
        } catch (err) {
            console.error("PageSignin.initData", err);
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
            this._btn_forgot_password.addEventListener('click', ly.lang.funcDebounce(this,
                this.onFldForgotPasswordClick, constants.DEBOUNCE_TIME_MS, true))
            this._btn_register.addEventListener('click', ly.lang.funcDebounce(this,
                this.onFldRegisterClick, constants.DEBOUNCE_TIME_MS, true))

            this._btn_signin.addEventListener('click', ly.lang.funcDebounce(this,
                this.onBtnSigninClick, constants.DEBOUNCE_TIME_MS, true));
        } catch (err) {
            console.error("PageSignin.initListeners", err);
        }
    }

    private removeListeners(): void {
        try {
            this._fld_logo.removeEventListener();
            this._fld_email.removeEventListener();
            this._fld_password.removeEventListener();
            this._btn_forgot_password.removeEventListener();
            this._btn_register.removeEventListener();

            this._btn_signin.removeEventListener();
        } catch (err) {
            console.error("PageSignin.removeListeners", err);
        }
    }

    private hasEmail(): boolean {
        return !!this._email;
    }

    private hasPassword(): boolean {
        return !!this._password;
    }

    private isValidEmail(): boolean {
        return ly.lang.isEmail(this._email);
    }

    private setView(): void {
        try {
            this._fld_email.classRemove('invalid');
            if (this.hasEmail() && !this.isValidEmail()) {
                this._fld_email.classAdd('invalid');
            }
        } catch (err) {
            console.error("PageSignin.setView", err);
        }
    }

    // events handler
    private onFldLogoClick(): void {
        try {
            // goto main
            Router.instance().goto(this.ROUTE_MAIN);
        } catch (err) {
            console.error("PageSignin.onBtnCompanyClick()", err)
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
            console.error("PageSignin.onFldEmailUpdate", err)
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
            console.error("PageSignin.onFldPasswordUpdate", err)
        }
    }

    private onFldForgotPasswordClick(e: Event): void {
        try {
            if (!!e) {
                e.preventDefault();
            }

            // goto reset password
            Router.instance().goto(this.ROUTE_RESET_PASSWORD);

        } catch (err) {
            console.error("PageSignin.onFldForgotPasswordClick", err)
        }
    }

    private onFldRegisterClick(e: Event): void {
        try {
            if (!!e) {
                e.preventDefault();
            }

            // goto register
            Router.instance().goto(this.ROUTE_SIGNUP);

        } catch (err) {
            console.error("PageSignin.onFldRegisterClick", err)
        }
    }

    private onBtnSigninClick(e: Event): void {
        try {
            if (!!e) {
                e.preventDefault();
            }

            if (!this.hasEmail() || !this.hasPassword()) {
                ToastController.instance().showError(ly.i18n.get('msg_email_and_password_required'));
            } else if (!this.isValidEmail()) {
                ToastController.instance().showError(ly.i18n.get('msg_email_not_valid'));
            } else {
                this.signin((error, user) => {
                    if (!!error) {
                        ToastController.instance().showError(error);
                    } else if (!!user) {
                        Router.instance().goto(this.ROUTE_HOME);
                    }
                });
            }

        } catch (err) {
            console.error("PageSignin.onBtnSigninClick", err)
        }
    }

    private signin(callback: Listener): void {

        if (!!this._email && !!this._password) {
            AuthController.instance().authenticate(
                this._email,
                this._password,
                (error, user) => {
                    try {
                        if (!!error) {
                            console.error("PageSignin.signin#api", error);
                            ly.lang.funcInvoke(callback, error, false);
                        } else {
                            ly.lang.funcInvoke(callback, false, user);
                        }
                    } catch (err) {
                        console.error("PageSignin.signin", err);
                        ly.lang.funcInvoke(callback, err, false);
                    }
                });
        }
    }


}