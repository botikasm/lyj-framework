import {EVENT_ON_ROUTE, Route, Router} from "../Router";
import Component from "../components/Component";
import Page from "./page/Page";
import ly from "../../ly";


abstract class PageController
    extends Component {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private _router: Router;

    private _last_page: Page;
    private _last_route: Route;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    constructor(root: string,
                hash: string = '') {
        super();

        this._router = new Router(root, hash);
        this._router.on(this, EVENT_ON_ROUTE, this.onRoute);
    }

    protected abstract route(page: Page): void;

    protected free(): void {
        this._router.stop();
        if (!!this._last_page) {
            this._last_page.remove();
        }
    }

    protected start(): void {
        this._router.start(this.element);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public get root(): string {
        return this._router.root;
    }

    public get isSolved(): boolean {
        return this._router.isSolved;
    }

    public get paused(): boolean {
        return this._router.paused;
    }

    public set paused(value: boolean) {
        this._router.paused = value;
    }

    public get debugMode(): boolean {
        return this._router.debugMode;
    }

    public set debugMode(value: boolean) {
        this._router.debugMode = value;
    }

    public register(route: string, handler: Function): void {
        this._router.register(route, handler);
    }

    public current(): Page {
        return this._last_page;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private onRoute(route: Route): void {
        try {
            const params: any = route.params;
            const func: any = route.handler;

            if (ly.lang.isFunction(func)) {
                if (ly.lang.isConstructor(route.handler)) {
                    // close last page
                    const last_page: Page = this._last_page;
                    if (!!last_page) {
                        last_page.hide();
                        ly.lang.funcDelay(() => {
                            last_page.remove();
                        }, 400);
                    }

                    this._last_route = route;
                    this._last_page = new func(route);
                    this._last_page.show();
                    this._router.relink(this._last_page.element);
                    this.route(this._last_page);
                } else {
                    // we have a callback
                    func(params);
                }
            }
        } catch (err) {
            console.error("PageController.onRoute", err);
        }
    }

}

// ------------------------------------------------------------------------
//                      e x p o r t s
// ------------------------------------------------------------------------

export default PageController;