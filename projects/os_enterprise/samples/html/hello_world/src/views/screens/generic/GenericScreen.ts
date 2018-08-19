import ElementWrapper from "../../../../vendor/lyts_core/view/components/ElementWrapper";
import console from "../../../../vendor/lyts_core/commons/console";
import view from "./view";
import {Route} from "../../../../vendor/lyts_core/view/Router";
import {Animate, AnimateEffect} from "../../../../vendor/lyts_core_style/styles/animate/Animate";
import Screen from "../../../../vendor/lyts_core/view/screens/screen/Screen";
import Page from "../../../../vendor/lyts_core/view/pages/page/Page";
import Page1 from "./pages/page1/Page1";
import Page2 from "./pages/page2/Page2";


export default class GenericScreen
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

        // register internal screen pages
        super.register('/page1', Page1);
        super.register('/page2/:param1/:param2', Page2);

        this._pages = super.getFirst("#" + this.uid + "_pages");
    }

    // ------------------------------------------------------------------------
    //                      o v e r r i d e
    // ------------------------------------------------------------------------

    protected route(page: Page): void {
        console.log('Screen2.route', page);
        page.appendTo(this._pages);
    }

    protected render(): string {
        return view(this.uid, {});
    }

    protected free(): void {

        // release memory

        console.log("REMOVED SCREEN2:", this.uid);
    }

    protected ready(): void {
        super.ready();

        this.init();
    }

    public show(): void {
        super.show();
        Animate.apply(AnimateEffect.fadeIn, this.element, () => {
            console.log('Screen2.show', AnimateEffect.bouce + ' animation terminated');
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
            console.error("Screen2.init()", err)
        }
    }


}