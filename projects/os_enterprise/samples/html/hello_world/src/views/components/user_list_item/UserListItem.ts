import Component from "../../../../vendor/lyts_core/view/components/Component";
import ly from "../../../../vendor/lyts_core/ly";

import view from "./view";
import ElementWrapper from "../../../../vendor/lyts_core/view/components/ElementWrapper";
import ModelAccount from "../../../model/ModelAccount";
import console from "../../../../vendor/lyts_core/commons/console";


export default class UserListItem
    extends Component {


    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static readonly ON_REMOVE = 'on_remove';

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private readonly _model_account: ModelAccount;

    private readonly _username: ElementWrapper;
    private readonly _password: ElementWrapper;

    private readonly _btn_remove: ElementWrapper;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    constructor(account: ModelAccount) {
        super();

        this._model_account = account;

        this._username = super.getFirst("#" + this.uid + "_username");
        this._password = super.getFirst("#" + this.uid + "_password");

        this._btn_remove = super.getFirst("#" + this.uid + "_btn_remove");
    }

    // ------------------------------------------------------------------------
    //                      o v e r r i d e
    // ------------------------------------------------------------------------

    protected render(): string {
        return view(this.uid, {});
    }

    protected free() {
        ly.Application.events.off(this);
        this._btn_remove.removeEventListener();
    }

    protected ready(): void {
        this.init();
    }

    public show() {

    }

    public hide() {

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private init(): void {

        // ly.Application.events.on(this, constants.EVENT_ON_LOGIN, this.onLogin);
        ly.lang.funcDelay(() => {
            // this.emit( this.OPEN_LAYER, { url: 'https://www.wikipedia.org/'} );
        }, 5000);

        this._btn_remove.addEventListener('click', this.onClickRemove);

        this.initModel();
    }

    private initModel(): void {
        if (!!this._model_account) {
            this._username.value(this._model_account.username);
            this._password.value(this._model_account.password);
        } else {
            console.error('UserListItem.initModel', 'MODEL IS EMPTY OR NULL');
        }
    }

    private onClickRemove(e: Event) {
        e.preventDefault();

        // emit event
        this.emit(UserListItem.ON_REMOVE, this._model_account);
    }

}