import AbstractService from "./AbstractService";
import i18n from "../../../vendor/lyts_core/view/i18n";
import AuthController from "../AuthController";
import constants from "../../constants";
import {RequestOptions, RequestResult} from "../../../vendor/lyts_core/net/HttpClient";

// ------------------------------------------------------------------------
//                      c o n s t
// ------------------------------------------------------------------------

const PATH: string = '/api/database/invoke/';
const DATABASE: string = constants.uid;

// ------------------------------------------------------------------------
//                      c l a s s
// ------------------------------------------------------------------------

/**
 * SAMPLE AUTHENTICATION SERVICE
 */
export default class AbstractDatabaseService
    extends AbstractService {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private readonly _collection: string;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    constructor(host: string, app_token: string, collection: string) {
        super(host, app_token);
        this._collection = collection;
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected get request() {
        return {
            'app_token': this.app_token,
            'lang': i18n.lang,
            'client_id': AuthController.instance().user_id,
            'database': DATABASE, // application name
            'collection': this._collection,
            'query': '',
            'params': {},
            'transform': {}
        };
    }

    public post(body: any = null,
                options?: RequestOptions): Promise<RequestResult> {
        return super.post(PATH, body, options);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}

