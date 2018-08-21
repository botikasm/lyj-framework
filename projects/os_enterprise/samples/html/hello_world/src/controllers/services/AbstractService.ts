import {RemoteService} from "../../../vendor/lyts_core/net/RemoteService";
import ly from "../../../vendor/lyts_core/ly";
import {RequestResult} from "../../../vendor/lyts_core/net/HttpClient";
import console from "../../../vendor/lyts_core/commons/console";

export type ServiceCallback = (error: any, response: any) => void;

export default class AbstractService
    extends RemoteService {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private readonly _app_token: string;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    constructor(host: string, app_token: string) {
        super(host);
        this._app_token = app_token;
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected get app_token(): string {
        return this._app_token;
    }

    protected invoke(callback: ServiceCallback, response: RequestResult): void {
        let error: any = this.getError(response);
        let data: any = this.getResponse(response);
        ly.lang.funcInvoke(callback, error, data);
    }

    protected getError(data: any): any {
        return AbstractService.errorFrom(data);
    }

    protected getResponse(data: any): any {
        return AbstractService.responseFrom(data);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static responseFrom(data: any): any {
        data = this.toJSON(data);
        let response = data;
        if (!!data && !!data.response) {
            response = data.response;
        }
        //console.log("AbstractService.getResponse()", data, response);
        if (!!response) {
            response = !!response.payload ? response.payload : response;
        }
        return response;
    }

    private static errorFrom(data: any): any {
        data = this.toJSON(data);
        console.log('AbstractService.errorFrom', data);
        if (!!data) {
            if (ly.lang.isString(data)) {
                return data;
            } else if (!!data.error) {
                return data.error;
            } else if (data.hasOwnProperty("ok") && !data.ok) {
                // RequestResult
                return data.statusText;
            } else if (!!data.data) {
                // nested data
                return AbstractService.errorFrom(data.data);
            } else if (!!data.response) {
                return AbstractService.errorFrom(data.response);
            }
        }
        return null;
    }


    private static toJSON(data: any): any {
        if (ly.lang.isString(data)) {
            try {
                data = JSON.parse(data);
            } catch (err) {
            }
        } else if (ly.lang.isFunction(data.json)) {
            // RequestResult
            let json_data = data.json();
            if (ly.objects.isEmpty(json_data)) {
                data = data.data;
            } else {
                data = json_data;
            }
        } else if (!!data.data) {
            // nested data
            data = data.data;
        }
        return data;
    }

}

