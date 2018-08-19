import {EVENT_ON_ROUTE, Route, Router} from "../Router";
import Component from "../components/Component";
import ly from "../../ly";
import Screen from "./screen/Screen";


/**
 * Control screens.
 * Screens can contain Pages.
 * Sample screen url: ./#!screen1
 * Sample screen url with page: ./#!screen1/page1 or ./#!screen1/page2
 */
abstract class ScreenController
    extends Component {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private _router: Router;

    private _last_screen: Screen;
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

    protected abstract route(page: Screen): void;

    protected free(): void {
        this._router.stop();
        if (!!this._last_screen) {
            this._last_screen.remove();
        }
    }

    protected ready(): void {
        this._router.start(this.element);

        this._init();
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
        this._router.register(route + '/*', handler);
    }

    public current(): Screen {
        return this._last_screen;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private _init(): void {

    }

    private onRoute(route: Route): void {
        try {
            const params: any = route.params;
            const func: any = route.handler;

            if (ly.lang.isFunction(func)) {
                if (ly.lang.isConstructor(route.handler)) {
                    // close last page
                    const last_page: Screen = this._last_screen;
                    if (!!last_page) {
                        last_page.hide();
                        ly.lang.funcDelay(() => {
                            last_page.remove();
                        }, 400);
                    }

                    this._last_route = route;
                    this._last_screen = new func(this.root, route); // screen ctr

                    if(!this._last_screen.isSolved){
                        this._last_screen.show();
                        this.route(this._last_screen);
                    }

                } else {
                    // we have a callback
                    func(params);
                }
            }
        } catch (err) {
            console.error("ScreenController.onRoute", err);
        }
    }

}

// ------------------------------------------------------------------------
//                      e x p o r t s
// ------------------------------------------------------------------------

export default ScreenController;