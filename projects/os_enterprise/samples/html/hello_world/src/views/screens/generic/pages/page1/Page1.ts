import ElementWrapper from "../../../../../../vendor/lyts_core/view/components/ElementWrapper";
import {Route} from "../../../../../../vendor/lyts_core/view/Router";
import view from "./view";
import Page from "../../../../../../vendor/lyts_core/view/pages/page/Page";
import ly from "../../../../../../vendor/lyts_core/ly";
import console from "../../../../../../vendor/lyts_core/commons/console";
import ToastController from "../../../../../controllers/ToastController";
import constants from "../../../../../constants";
import ServiceController from "../../../../../controllers/ServiceController";


export default class Page1
    extends Page {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private readonly _lbl_version: ElementWrapper;

    private readonly _btn_register: ElementWrapper;
    private readonly _fld_register_username: ElementWrapper;
    private readonly _fld_register_password: ElementWrapper;

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
        } catch (err) {
            console.error("Page1.init()", err)
        }
    }

    private onRegisterClick(ev: Event): void {
        ev.preventDefault();
        const self: Page1 = this;
        ly.dom.ready(function () {
            self.doRegister();
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
                        console.log('Page1.doRegister', response);
                    }
                });
            } else {
                this.logError('Page1.doRegister', 'Missing username or password');
            }
        } catch (err) {
            console.error('Page1.doRegister', err);
        }
    }


    private logError(scope: string, err: any): void {
        console.error(scope, err);
        ToastController.instance().showError(err);
    }


}