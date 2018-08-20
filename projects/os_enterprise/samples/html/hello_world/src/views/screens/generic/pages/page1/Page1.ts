import ElementWrapper from "../../../../../../vendor/lyts_core/view/components/ElementWrapper";
import {Route} from "../../../../../../vendor/lyts_core/view/Router";
import view from "./view";
import Page from "../../../../../../vendor/lyts_core/view/pages/page/Page";
import ly from "../../../../../../vendor/lyts_core/ly";
import console from "../../../../../../vendor/lyts_core/commons/console";
import ToastController from "../../../../../controllers/ToastController";
import constants from "../../../../../constants";
import ServiceController from "../../../../../controllers/ServiceController";
import UserListItem from "../../../../components/user_list_item/UserListItem";
import Component from "../../../../../../vendor/lyts_core/view/components/Component";


export default class Page1
    extends Page {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private readonly _lbl_version: ElementWrapper;

    private readonly _btn_register: ElementWrapper;
    private readonly _fld_register_username: ElementWrapper;
    private readonly _fld_register_password: ElementWrapper;

    private readonly _btn_login: ElementWrapper;
    private readonly _fld_login_username: ElementWrapper;
    private readonly _fld_login_password: ElementWrapper;

    private readonly _user_list: ElementWrapper;

    private _components: Array<Component>;
    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    constructor(route: Route) {
        super(route);

        this._lbl_version = super.getFirst("#version");

        // register
        this._fld_register_username = super.getFirst("#" + this.uid + "_fld_register_username");
        this._fld_register_password = super.getFirst("#" + this.uid + "_fld_register_password");
        this._btn_register = super.getFirst("#" + this.uid + "_btn_register");

        // login
        this._fld_login_username = super.getFirst("#" + this.uid + "_fld_login_username");
        this._fld_login_password = super.getFirst("#" + this.uid + "_fld_login_password");
        this._btn_login = super.getFirst("#" + this.uid + "_btn_login");

        // list of all users
        this._user_list = super.getFirst("#" + this.uid + "_user_list");

        this._components = [];
    }

    // ------------------------------------------------------------------------
    //                      o v e r r i d e
    // ------------------------------------------------------------------------

    protected render(): string {
        return view(this.uid, {});
    }

    protected free(): void {
        // release memory
        this._btn_register.removeEventListener();
        this._btn_login.removeEventListener();

        this.finishUserList();

        console.log("REMOVED:", this.uid);
    }

    protected ready(): void {
        this.init();
    }

    public show(): void {
        super.show();

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
            this._lbl_version.innerHTML = constants.version;

            // call debounced function
            this._btn_register.addEventListener('click',
                ly.lang.funcDebounce(this, this.onRegisterClick, 1000, true));
            this._btn_login.addEventListener('click',
                ly.lang.funcDebounce(this, this.onLoginClick, 1000, true));
        } catch (err) {
            console.error("Page1.init()", err)
        }

        // add users to users list
        this.loadUsersList();
    }

    private finishUserList(): void {
        // remove content
        this._user_list.innerHTML = '';
        for (let comp of this._components) {
            comp.off(this);
        }
        this._components = [];
    }

    private logError(scope: string, err: any): void {
        console.error(scope, err);
        ToastController.instance().showError(err);
    }

    private onRegisterClick(ev: Event): void {
        ev.preventDefault();
        const self: Page1 = this;
        ly.dom.ready(function () {
            self.doRegister();
        }, this);
    }

    private onLoginClick(ev: Event): void {
        ev.preventDefault();
        const self: Page1 = this;
        ly.dom.ready(function () {
            self.doLogin();
        }, this);
    }

    private doRegister(): void {
        try {
            const username: string = this._fld_register_username.value();
            const password: string = this._fld_register_password.value();

            if (!!username && !!password) {
                ServiceController.instance().account.register(username, password, (err, response) => {
                    if (!!err) {
                        this.logError('Page1.doRegister', err);
                    } else {
                        // console.log('Page1.doRegister', response);
                        this.loadUsersList();
                    }
                });
            } else {
                this.logError('Page1.doRegister', 'Missing username or password');
            }
        } catch (err) {
            console.error('Page1.doRegister', err);
        }
    }

    private doLogin(): void {
        try {
            const username: string = this._fld_login_username.value();
            const password: string = this._fld_login_password.value();

            if (!!username && !!password) {
                ServiceController.instance().account.login(username, password, (err, response) => {
                    if (!!err) {
                        this.logError('Page1.doLogin', err);
                    } else {
                        console.log('Page1.doLogin', response);
                        // this.loadUsersList();
                    }
                });
            } else {
                this.logError('Page1.doLogin', 'Missing username or password');
            }
        } catch (err) {
            console.error('Page1.doLogin', err);
        }
    }


    private loadUsersList(): void {
        // remove content
        this.finishUserList();

        // get all users
        ServiceController.instance().account.get_all_accounts(0, 100, (err, response) => {
            if (!!err) {
                this.logError('Page1.loadUsersList', err);
            } else {
                try {
                    console.log('Page1.loadUsersList', response);
                    for (let i = 0; i < response.length; i++) {
                        const item = response[i];
                        this.loadUserListItem(item);
                    }
                } catch (err) {
                    this.logError('Page1.loadUsersList', err);
                }
            }
        });
    }

    private loadUserListItem(account: any): void {
        console.log('Page1.loadUserListItem', account);
        const comp: UserListItem = new UserListItem(account);
        comp.appendTo(this._user_list);
        this._components.push(comp);

        // event
        comp.on(this, UserListItem.ON_REMOVE, this.doRemoveAccountItem);
    }

    private doRemoveAccountItem(account: any): void {
        if (!!account) {
            //console.log('Page1.doRemoveAccountItem', account);
            ServiceController.instance().account.remove_account(account._key, (err, response) => {
                // reload
                this.loadUsersList();
            });
        }
    }
}