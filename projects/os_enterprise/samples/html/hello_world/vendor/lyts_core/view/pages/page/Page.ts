import console from "../../../commons/console";
import {Route} from "../../Router";
import Component from "../../components/Component";


abstract class Page
    extends Component {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private readonly _params: any;
    private readonly _name: string;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    constructor(route: Route) {
        super();
        try {
            this._name = this.uid;
            if (!!route) {
                this._params = route.params;
                this._name = route.uid();
            }
        } catch (err) {
            console.error("Page.construcotr", err);
        }
    }

    protected abstract render(): string;

    protected abstract ready(): void;

    protected abstract free(): void;

    public show(): void {
        this.element.classRemove('hide');
    }

    public hide(): void {
        this.element.classAdd('hide');
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public get name() {
        return this._name;
    }

    /**
     * Return url parameters if any
     */
    public get params() {
        return !!this._params ? this._params : false;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}

// ------------------------------------------------------------------------
//                      e x p o r t s
// ------------------------------------------------------------------------

export default Page;