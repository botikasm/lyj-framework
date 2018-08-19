import AbstractService, {ServiceCallback} from "./AbstractService";
import lang from "../../../vendor/lyts_core/commons/lang";

const PATH: string = '/api/account/';

/**
 * SAMPLE AUTHENTICATION SERVICE
 */
export default class AuthenticationService
    extends AbstractService {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    constructor(host: string, app_token: string) {
        super(host, app_token);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public get_account(key: string, callback: ServiceCallback): void {
        let data = {
            'app_token': this.app_token,
            'key': key
        };
        super.post(PATH + "get_account", data).then((req_resp) => {
            super.invoke(callback, req_resp);
        }).catch((req_resp) => {
            super.invoke(callback, req_resp);
        });
    }

    public login(email: string, password: string, callback: ServiceCallback): void {
        let data = {
            'app_token': this.app_token,
            'email': email,
            'password': password
        };
        super.post(PATH + "login", data).then((req_resp) => {
            super.invoke(callback, req_resp);
        }).catch((req_resp) => {
            super.invoke(callback, req_resp);
        });
    }

    public register(email: string, password: string, callback: ServiceCallback): void {
        let data = {
            'app_token': this.app_token,
            'email': email,
            'password': password
        };
        super.post(PATH + "register", data).then((req_resp) => {
            super.invoke(callback, req_resp);
        }).catch((req_resp) => {
            super.invoke(callback, req_resp);
        });
    }

    public reset_password(email: string, callback: ServiceCallback): void {
        let data = {
            'app_token': this.app_token,
            'email': email
        };
        super.post(PATH + "reset_password", data).then((req_resp) => {
            super.invoke(callback, req_resp);
        }).catch((req_resp) => {
            super.invoke(callback, req_resp);
        });
    }

    public upsert(item: any, callback: ServiceCallback): void {
        let data = {
            'app_token': this.app_token,
            'item': lang.isString(item) ? item : lang.toString(item)
        };
        super.post(PATH + "upsert", data).then((req_resp) => {
            super.invoke(callback, req_resp);
        }).catch((req_resp) => {
            super.invoke(callback, req_resp);
        });
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}

