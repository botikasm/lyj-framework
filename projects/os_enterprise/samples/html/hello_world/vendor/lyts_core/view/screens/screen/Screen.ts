import console from "../../../commons/console";
import {Route} from "../../Router";
import PageController from "../../pages/PageController";
import paths from "../../../commons/paths";


abstract class Screen
    extends PageController {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private readonly _parent_route: Route;
    private readonly _params: any;
    private readonly _name: string;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    constructor(root: string, route: Route) {
        super(root);
        this._parent_route = route;
        try {
            this._name = this.uid;
            if (!!route) {
                this._params = route.params;
                this._name = route.uid();
            }
        } catch (err) {
            console.error("Screen.constructor", err);
        }
    }

    protected abstract render(): string;

    protected abstract free(): void;

    protected ready(): void {
        this.start();
    }

    public register(route: string, handler: Function): void {
        super.register(this.concatParent(route), handler);
    }

    public show(): void {
        this.paused = false;
        this.element.classRemove('hide');
    }

    public hide(): void {
        this.paused = true; // pause page controller
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

    private parentPath(): string {
        const parent_path = this._parent_route.path;
        if (parent_path.indexOf('*') > -1) {
            return parent_path.substring(0, parent_path.length - 2);
        }
        return parent_path;
    }

    private concatParent(path: string): string {
        const parent_path = this.parentPath();
        return paths.concat(parent_path, path);
    }

}

// ------------------------------------------------------------------------
//                      e x p o r t s
// ------------------------------------------------------------------------

export default Screen;