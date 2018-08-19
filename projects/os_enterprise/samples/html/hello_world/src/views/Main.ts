import console from "../../vendor/lyts_core/commons/console";
import ElementWrapper from "../../vendor/lyts_core/view/components/ElementWrapper";
import ly from "../../vendor/lyts_core/ly";
import ScreenController from "../../vendor/lyts_core/view/screens/ScreenController";
import Screen from "../../vendor/lyts_core/view/screens/screen/Screen";
import constants from "../constants";
import view from "./view";
import en from "../i18n/en";
import it from "../i18n/it";
import AuthScreen from "./screens/auth/AuthScreen";
import GenericScreen from "./screens/generic/GenericScreen";

export default class Main
    extends ScreenController {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private readonly _body: ElementWrapper;
    private readonly _loader: ElementWrapper;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    constructor() {
        super("");

        // set debug mode
        this.debugMode = true;

        // customize console
        console.uid = constants.uid;

        // register screens
        super.register('/generic', GenericScreen);
        super.register('/auth', AuthScreen);


        this._body = super.getFirst("#" + this.uid + "_screens");
        this._loader = new ElementWrapper(this, ly.dom.getFirst("#_app_loader"));
    }

    // ------------------------------------------------------------------------
    //                      o v e r r i d e
    // ------------------------------------------------------------------------

    protected render(): string {
        return view(this.uid, {});
    }

    protected free(): void {
        super.free();
        // release memory


        console.log('Main.free()', "REMOVED MAIN: " + constants.uid);
    }

    protected ready(): void {
        // console.log('Main.ready', "INIT");
        try {
            this.init();

            if (!!this._loader) {
                this._loader.remove();
            } else {
                console.warn('Main.ready', 'this._loader is null.');
            }
        } catch (err) {
            console.error("Main.ready", err);
        }
        super.ready();
    }

    public show(): void {

    }

    public hide(): void {

    }

    public route(screen: Screen) {
        screen.appendTo(this._body);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private init(): void {

        try {
            // App localizations
            this.initI18n();

            // event handlers
            this.initHandlers();


        } catch (err) {
            console.error("Main.init()", err)
        }

    }

    private initI18n(): void {
        //-- load i18n dictionaries --//
        ly.i18n.registerDefault(en);
        ly.i18n.register("en", en);
        ly.i18n.register("it", it);

    }

    private initHandlers(): void {

    }


}