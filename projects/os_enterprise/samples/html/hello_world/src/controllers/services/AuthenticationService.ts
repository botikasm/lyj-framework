import {ServiceCallback} from "./AbstractService";
import lang from "../../../vendor/lyts_core/commons/lang";
import AbstractDatabaseService from "./AbstractDatabaseService";
import i18n from "../../../vendor/lyts_core/view/i18n";
import ModelAccount from "../../model/ModelAccount";
import ly from "../../../vendor/lyts_core/ly";

// ------------------------------------------------------------------------
//                      c o n s t
// ------------------------------------------------------------------------

const COLLECTION: string = 'accounts';

// ------------------------------------------------------------------------
//                      c l a s s
// ------------------------------------------------------------------------

/**
 * SAMPLE AUTHENTICATION SERVICE
 */
export default class AuthenticationService
    extends AbstractDatabaseService {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    constructor(host: string, app_token: string) {
        super(host, app_token, COLLECTION);

        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public get_all_accounts(skip: number, limit: number, callback: ServiceCallback): void {
        const request_data = this.request;
        request_data.query = '#findEqualAsc';
        request_data.params = JSON.stringify({
            skip: skip,
            limit: limit,
            sort: ['username', 'password']
        });

        super.post(request_data).then((req_resp) => {
            super.invoke(callback, req_resp);
        }).catch((req_resp) => {
            super.invoke(callback, req_resp);
        });
    }

    public get_account(key: string, callback: ServiceCallback): void {
        const request_data = this.request;

        super.post(request_data).then((req_resp) => {
            super.invoke(callback, req_resp);
        }).catch((req_resp) => {
            super.invoke(callback, req_resp);
        });
    }

    public remove_account(key: string, callback: ServiceCallback): void {
        const request_data = this.request;
        request_data.query = '#remove';
        request_data.params = JSON.stringify({
            _key: key
        });

        super.post(request_data).then((req_resp) => {
            super.invoke(callback, req_resp);
        }).catch((req_resp) => {
            super.invoke(callback, req_resp);
        });
    }

    public register(email: string,
                    password: string,
                    callback: ServiceCallback): void {

        // new account model
        const account: ModelAccount = {
            _key: ly.random.guid(),
            username: email,
            password: password,
            lang: i18n.lang,
            email: email,
            address: '',
            first_name: '',
            last_name: '',
            phone: '',
            is_enabled: true
        };

        // build request
        const request_data = this.request;
        request_data.query = '#upsert';
        request_data.params = JSON.stringify(account);
        request_data.transform = JSON.stringify({
            'password':'md5'
        });

        super.post(request_data).then((req_resp) => {
            super.invoke(callback, req_resp);
        }).catch((req_resp) => {
            super.invoke(callback, req_resp);
        });
    }

    public login(email: string, password: string, callback: ServiceCallback): void {

        const request_data = this.request;
        request_data.query = '#findOneEqual';
        request_data.params = JSON.stringify({
            username: email,
            password: password
        });
        request_data.transform = JSON.stringify({
            'password':'md5'
        });

        super.post(request_data).then((req_resp) => {
            super.invoke(callback, req_resp);
        }).catch((req_resp) => {
            super.invoke(callback, req_resp);
        });
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private init(): void {
        // add keys
        const request_data = this.request;
        request_data.query = '#addIndex';
        request_data.params = JSON.stringify({
            'fields': ['username', 'password'],
            'is_unique': true
        });

        super.post(request_data).then((req_resp) => {
            //super.invoke(callback, req_resp);
        }).catch((req_resp) => {
            //super.invoke(callback, req_resp);
        });
    }

}

