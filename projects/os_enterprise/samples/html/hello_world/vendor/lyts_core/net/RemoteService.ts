import strings from "../commons/strings";
import {HttpClient, RequestOptions, RequestResult} from "./HttpClient";

export class RemoteService {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private _host: string;
    private _client: HttpClient;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    constructor(host: string) {
        this._host = strings.endWith(host, "/") ? host.substring(0, host.length - 1) : host;
        this._client = new HttpClient();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public get(path: string,
               queryParams: any = {},
               options?: RequestOptions): Promise<RequestResult> {
        const url = this.url(path);
        return this._client.send("get", url, queryParams, null, options);
    }

    public post(path: string,
                body: any = null,
                options?: RequestOptions): Promise<RequestResult> {
        const url = this.url(path);
        return this._client.send("post", url, {}, body, options);
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    private url(path: string): string {
        if (path.indexOf("/") === 0) {
            path = path.substring(1);
        }
        return this._host + "/" + path;
    }

}