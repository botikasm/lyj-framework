import ly from "../lyts_core/ly";
import {Dictionary} from "../lyts_core/commons/collections/Dictionary";

/**
 * Supported modules
 */
enum StyleModule {

    animate = 'animate',   // animate css

}

/**
 * Singleton manager for application styles.
 * StyleManager allow to inject styles into html head
 */
class StyleManagerClass {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private readonly _registered_modules: Dictionary<string | Function>;

    private _use_one_style_tag: boolean;
    private _hystory: string[];

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    constructor() {
        this._registered_modules = new Dictionary<string | Function>();
        this._use_one_style_tag = false;
        this._hystory = [];
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public get useOneStyleTag(): boolean {
        return this._use_one_style_tag;
    }

    public set useOneStyleTag(value: boolean) {
        this._use_one_style_tag = value;
    }

    /**
     * Register a style module content or proxy (function called when content is required)
     * @param {string} module Module name
     * @param {string | Function} proxy CSS Content or function reference
     */
    public register(module: string, proxy: string | Function): StyleManagerClass {
        this._registered_modules.put(module, proxy);
        return this;
    }

    /**
     * Inject style directly to head
     * @param props
     * @param {StyleModule} modules
     */
    public inject(props: any, ...modules: string[]): void {
        if (this._use_one_style_tag) {
            this.injectOne(props, ...modules);
        } else {
            this.injectAll(props, ...modules);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private loadModule(props: any, module: string): string {
        const proxy: string | Function = this._registered_modules.get(module);
        const module_content: string = ly.lang.isFunction(proxy) ? (proxy as Function)(props) : proxy;
        if (!!module_content) {
            return module_content
                .split('<style>').join('\n')
                .split('</style>').join('\n')
                .trim();
        }
        return '';
    }

    private injectOne(props: any, ...modules: string[]): void {
        // creates css directives
        let css = '';
        for (let module of modules) {
            if (this._hystory.indexOf(module) === -1) {
                css += this.loadModule(props, module);
                this._hystory.push(module);
            }
        }

        // add line
        css = '\n' + css + '\n';

        ly.dom.injectStyle(css);
    }

    private injectAll(props: any, ...modules: string[]): void {
        // creates css directives
        for (let module of modules) {
            if (this._hystory.indexOf(module) === -1) {
                const css = this.loadModule(props, module);
                this._hystory.push(module);
                ly.dom.injectStyle(css);
            }
        }
    }


    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static __instance: StyleManagerClass;

    public static instance(): StyleManagerClass {
        if (null == StyleManagerClass.__instance) {
            StyleManagerClass.__instance = new StyleManagerClass();
        }
        return StyleManagerClass.__instance;
    }

}

// ------------------------------------------------------------------------
//                      e x p o r t s
// ------------------------------------------------------------------------

const StyleManager: StyleManagerClass = StyleManagerClass.instance();

export {StyleManager, StyleModule}
