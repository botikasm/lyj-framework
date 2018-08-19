import Page from "../../../../../../vendor/lyts_core/view/pages/page/Page";
import ElementWrapper from "../../../../../../vendor/lyts_core/view/components/ElementWrapper";
import console from "../../../../../../vendor/lyts_core/commons/console";
import {Route} from "../../../../../../vendor/lyts_core/view/Router";
import {Animate, AnimateEffect} from "../../../../../../vendor/lyts_core_style/styles/animate/Animate";
import view from "./view";


export default class Page2
    extends Page {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private readonly _content: ElementWrapper;


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    constructor(route: Route) {
        super(route);

        this._content = super.getFirst("#" + this.uid + "_content");
    }

    // ------------------------------------------------------------------------
    //                      o v e r r i d e
    // ------------------------------------------------------------------------

    protected render(): string {
        return view(this.uid, {});
    }

    protected free(): void {

        // release memory

        console.log("REMOVED:", this.uid);
    }

    protected ready(): void {
        this.init();
    }

    public show(): void {
        Animate.apply(AnimateEffect.fadeIn, this.element, () => {
            console.log('Page2.show', AnimateEffect.fadeIn + ' animation terminated');
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
            console.error("Page2.init()", err)
        }
    }


}