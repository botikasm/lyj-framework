/**
 * Authentication controller.
 *
 * Call authenticate() to login user or auto-login if cookies are stored.
 * Each time the user is authenticated the system do:
 *  - store user in memory (AuthController.user)
 *  - set user language into i18n
 *  - trigger ON_LOGIN event
 *
 *  Call exit() to logout.
 *  If you need to clean cookies, user exit(true).
 *
 */

import ServiceController from "./ServiceController";
import {ServiceCallback} from "./services/AbstractService";
import lang from "../../vendor/lyts_core/commons/lang";
import cookies from "../../vendor/lyts_core/view/cookies";
import ly from "../../vendor/lyts_core/ly";
import constants from "../constants";
import errors from "./utils/errors";
import ModelAccount from "../model/ModelAccount";

export default class AuthController {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private readonly COOKIE_USER_ID: string = 'user_id';
    private readonly COOKIE_USERNAME: string = 'username';
    private readonly COOKIE_USER_LANG: string = 'user_lang';

    private _user: ModelAccount | null;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private constructor() {

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    /**
     * Current authorized user
     * @return User
     */
    public get user(): ModelAccount | null {
        return this._user;
    }

    public get user_id(): string {
        return !!this._user ? this._user._key : cookies.read(this.COOKIE_USER_ID);
    }

    public get username(): string {
        return !!this._user ? this._user.username : cookies.read(this.COOKIE_USERNAME);
    }

    public get lang(): string {
        return !!this._user ? this._user.lang : cookies.read(this.COOKIE_USER_LANG) || ly.i18n.lang;
    }

    /**
     * Try to authenticate user.
     * Each time this method find a valid accounts, trigger the ON_LOGIN event.
     * Call this method without any parameter for autologin.
     * @param {string} username  (Optional)
     * @param {string} password (Optional)
     * @param {ServiceCallback} callback Callback to handle response
     */
    public authenticate(username?: string, password?: string, callback?: ServiceCallback): void {
        if (!!username && !!password) {
            this.services.account.login(username, password, (error: any, response: any) => {
                if (!error) {
                    // save cookies
                    this.store(response);

                    // trigger global login event
                    ly.Application.events.emit(constants.EVENT_ON_LOGIN, response);
                }
                if (!!callback) {
                    lang.funcInvoke(callback, errors.getMessage(error), response);
                }
            });
        } else if (!!this._user) {
            // trigger global login event
            ly.Application.events.emit(constants.EVENT_ON_LOGIN, this._user);
            if (!!callback) {
                lang.funcInvoke(callback, null, this._user);
            }
        } else if (!!this.user_id) {
            this.services.account.get_account(this.user_id, (error: any, response: any) => {
                if (!error) {
                    // save cookies
                    this.store(response);

                    // trigger global login event
                    ly.Application.events.emit(constants.EVENT_ON_LOGIN, response);
                }
                if (!!callback) {
                    lang.funcInvoke(callback, errors.getMessage(error), response);
                }
            });
        } else {
            if (!!callback) {
                lang.funcInvoke(callback, "User Not Logged: Login Required!", null);
            }
        }
    }

    public register(username: string, password: string, callback?: ServiceCallback) {
        if (!!callback && !!username && !!password) {
            try {
                this.services.account.register(username, password, (error: any, response: any) => {
                    if (!error) {
                        // save cookies
                        this.store(response);

                        // trigger global login event
                        ly.Application.events.emit(constants.EVENT_ON_LOGIN, response);
                    }
                    lang.funcInvoke(callback, errors.getMessage(error), response);
                });
            } catch (err) {
                lang.funcInvoke(callback, errors.getMessage(err), null);
            }
        }
    }

    public setAuthUser(user: ModelAccount): void {
        if (!!user) {
            this.store(user);
        }
    }

    /**
     * Logout
     * @param {boolean} clear_cache If true remove cookies and everything on this user in memory
     */
    public exit(clear_cache: boolean = false): void {
        if (clear_cache) {
            this.clearCookies();
        }
        this._user = null;
        // trigger global login event
        ly.Application.events.emit(constants.EVENT_ON_LOGOUT);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private get services(): ServiceController {
        return ServiceController.instance();
    }

    private store(user: any) {
        if (!!user && !!user._key) {

            // save locally in memory
            this._user = user;

            // save cookies
            cookies.create(this.COOKIE_USER_ID, user._key, 20);
            cookies.create(this.COOKIE_USERNAME, user.username, 20);
            cookies.create(this.COOKIE_USER_LANG, user.lang, 20);

            // set user lang into i18n controller
            ly.i18n.lang = user.lang;

            // console.log('AuthController.store', this._user);
        }
    }

    private clearCookies(): void {
        cookies.erase(this.COOKIE_USER_ID);
        cookies.erase(this.COOKIE_USERNAME);
        cookies.erase(this.COOKIE_USER_LANG);
    }

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static __instance: AuthController;

    public static instance(): AuthController {
        if (null == AuthController.__instance) {
            AuthController.__instance = new AuthController();
        }
        return AuthController.__instance;
    }

}