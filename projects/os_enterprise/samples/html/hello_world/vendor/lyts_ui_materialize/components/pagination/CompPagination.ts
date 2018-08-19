import ly from "../../../lyts_core/ly";
import Component from "../../../lyts_core/view/components/Component";
import ElementWrapper from "../../../lyts_core/view/components/ElementWrapper";
import view from "./CompPaginationView";
import console from "../../../lyts_core/commons/console";


const PREV_ITEM: string = 'p';
const NEXT_ITEM: string = 'n';

export default class CompPagination
    extends Component {

    // ------------------------------------------------------------------------
    //                      c o n s t a n t s
    // ------------------------------------------------------------------------
    public static readonly ON_PAGE_CHANGED: string = 'on_page_changed';

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------
    private _size: number;
    private _curr_page: number;
    private _items: Array<ElementWrapper>;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------
    constructor() {
        super();
        //
        this._size = 0;
        this._curr_page = 1;
        this._items = new Array<ElementWrapper>();
    }

    // ------------------------------------------------------------------------
    //                      o v e r r i d e
    // ------------------------------------------------------------------------
    protected render(): string {
        return view(this.uid, {});
    }

    protected free(): void {
        this.clearItems();
    }

    protected ready(): void {
    }


    public show() {

    }

    public hide() {

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------
    public set size(value: number) {
        this._size = value;
        this._curr_page = 1;
        this.initItems();
    }

    public get size(): number {
        return this._size;
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private initItems(): void {
        this.clearItems();
        this.createItems();
        this.refreshItems();
    }

    private clearItems(): void {
        try {
            if (!!this._items) {
                this._items.forEach((item) => {
                    item.removeEventListener();
                    if (!!item.htmlElement) {
                        item.htmlElement.remove();
                    }
                });
                // clear items array
                this._items = [];
            }
        } catch (err) {
            console.error("CompPagination.clearItems()", err);
        }
    }

    private createItems(): void {
        try {
            // prev item (go to prev item)
            const prev_item: ElementWrapper = new ElementWrapper(this, ly.dom.newElement(
                '<li data-page="' + PREV_ITEM + '"><a><i data-page="' + PREV_ITEM + '" class="material-icons">chevron_left</i></a></li>'));
            prev_item.appendTo(this.element);
            this._items.push(prev_item);
            prev_item.addEventListener('click', this.onChangePage);

            // items
            for (let i = 0; i < this._size; i++) {
                const item_data_page: string = (i + 1) + '';
                const item: ElementWrapper = new ElementWrapper(this, ly.dom.newElement(
                    '<li data-page="' + item_data_page + '"><a data-page="' + item_data_page + '">' + item_data_page + '</a></li>'));
                item.appendTo(this.element);
                this._items.push(item);
                item.addEventListener('click', this.onChangePage);
            }

            // next item (go to next item)
            const next_item: ElementWrapper = new ElementWrapper(this, ly.dom.newElement(
                '<li data-page="' + NEXT_ITEM + '" ><a><i data-page="' + NEXT_ITEM + '" class="material-icons">chevron_right</i></a></li>'));
            next_item.appendTo(this.element);
            this._items.push(next_item);
            next_item.addEventListener('click', this.onChangePage);

        } catch (err) {
            console.error("CompPagination.createItems()", err);
        }
    }

    private refreshItems(): void {
        // loop on items
        try {
            if (!!this._items) {
                this._items.forEach((item: ElementWrapper) => {
                    // remove all class
                    item.classRemove('disabled');
                    item.classRemove('waves-effect');
                    item.classRemove('active');
                    const item_data_page: string | null = item.getAttribute('data-page');
                    if (!!item_data_page) {
                        // prev item
                        if (PREV_ITEM === item_data_page) {
                            if (this._curr_page > 1 && this._size > 0) {
                                item.classAdd('waves-effect');
                            } else {
                                item.classAdd('disabled');
                            }
                            // next item
                        } else if (NEXT_ITEM === item_data_page) {
                            if (this._curr_page < this._size && this._size > 0) {
                                item.classAdd('waves-effect');
                            } else {
                                item.classAdd('disabled');
                            }
                            // other items
                        } else {
                            if (this._curr_page === ly.lang.toInt(item_data_page)) {
                                item.classAdd('active');
                            } else {
                                item.classAdd('waves-effect');
                            }
                        }
                    }
                });
            }
        } catch (err) {
            console.error("CompPagination.refresh()", err);
        }

    }

    private onChangePage(e: Event): void {
        try {

            e.preventDefault();

            const element: Element | null = e.srcElement;
            if (!!element) {
                // delay for show wave-effect
                ly.lang.funcDelay(() => {
                    const element_data_page: string | null = element.getAttribute('data-page');
                    this._curr_page = this.calculateCurrPage(element_data_page);
                    this.refreshItems();
                    super.emit(CompPagination.ON_PAGE_CHANGED, this._curr_page);
                }, 300);

            }


        } catch (err) {
            console.error("CompPagination.onChangePage()", err);
        }
    }

    private calculateCurrPage(element_data_page: string | null): number {
        if (!!element_data_page) {
            if (PREV_ITEM === element_data_page && this._curr_page - 1 >= 1) {
                return this._curr_page - 1;
            } else if (NEXT_ITEM === element_data_page && this._curr_page + 1 <= this._size) {
                return this._curr_page + 1;
            } else {
                const i_data_page = ly.lang.toInt(element_data_page);
                if (i_data_page <= this._size && i_data_page >= 1) {
                    return i_data_page;
                }
            }
        }
        return 1;
    }

}