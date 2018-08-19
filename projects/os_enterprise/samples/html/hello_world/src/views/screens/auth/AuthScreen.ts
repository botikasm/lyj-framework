import ElementWrapper from "../../../../vendor/lyts_core/view/components/ElementWrapper";
import console from "../../../../vendor/lyts_core/commons/console";
import view from "./view";
import {Route} from "../../../../vendor/lyts_core/view/Router";
import {Animate, AnimateEffect} from "../../../../vendor/lyts_core_style/styles/animate/Animate";
import Screen from "../../../../vendor/lyts_core/view/screens/screen/Screen";
import Page from "../../../../vendor/lyts_core/view/pages/page/Page";
import PageSignin from "./pages/signin/PageSignin";
import PageSignup from "./pages/signup/PageSignup";


export default class AuthScreen
    extends Screen {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private readonly _pages: ElementWrapper;


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    constructor(root: string, route: Route) {
        super(root, route);

        this.debugMode = true;

        // register internal screen pages
        super.register('/signin', PageSignin);
        super.register('/signup', PageSignup);

        this._pages = super.getFirst("#" + this.uid + "_pages");
    }

    // ------------------------------------------------------------------------
    //                      o v e r r i d e
    // ------------------------------------------------------------------------

    protected route(page: Page): void {
        page.appendTo(this._pages);
    }

    protected render(): string {
        return view(this.uid, {});
    }

    protected free(): void {

        // release memory

        console.log("REMOVED AUTH SCREEN:", this.uid);
    }

    protected ready(): void {
        super.ready();

        this.init();
    }

    public show(): void {
        super.show();
        Animate.apply(AnimateEffect.slideInDown, this.element, () => {
            console.log('AuthScreen.show', AnimateEffect.bouce + ' animation terminated');
        });
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


        } catch (err) {
            console.error("AuthScreen.init()", err)
        }
    }


}