import {RemoteService} from "../../../vendor/lyts_core/net/RemoteService";
import ly from "../../../vendor/lyts_core/ly";
import {RequestResult} from "../../../vendor/lyts_core/net/HttpClient";

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
        if (ly.lang.isString(data)) {
            try {
                return (<{ error: any }>JSON.parse(data)).error;
            } catch (err) {
            }
        } else if (!!data.error) {
            return data.error;
        } else if (data.hasOwnProperty("ok") && !data.ok) {
            // RequestResult
            return data.statusText;
        } else if (!!data.data) {
            // nested data
            return this.getError(data.data);
        }
        return null;
    }

    protected getResponse(data: any): any {
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
        return AbstractService.responseFrom(data);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static responseFrom(data: any): any {
        let response = data;
        if (!!data && !!data.response) {
            response = data.response;
        }
        //console.log("AbstractService.getResponse()", data, response);
        return response;
    }

}

