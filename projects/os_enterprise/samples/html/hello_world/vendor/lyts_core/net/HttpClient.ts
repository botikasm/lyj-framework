export interface RequestOptions {
    ignoreCache?: boolean;
    headers?: { [key: string]: string };
    // 0 (or negative) to wait forever
    timeout?: number;
}

export const DEFAULT_REQUEST_OPTIONS: RequestOptions = {
    ignoreCache: false,
    headers: {
        Accept: 'application/json, text/javascript, text/plain, */*; q=0.01',
    },
    // default max duration for a request
    timeout: 5000,
};

export interface RequestResult {
    ok: boolean;
    status: number;
    statusText: string;
    data: string;
    json: <T>() => T;
    headers: string;
}

export class HttpClient {

    // ------------------------------------------------------------------------
    //                      C O N S T
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    constructor() {

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public send(method: 'get' | 'post',
                url: string,
                queryParams: any = {},
                body: any = null,
                options: RequestOptions = DEFAULT_REQUEST_OPTIONS): Promise<RequestResult> {

        const ignoreCache = options.ignoreCache || DEFAULT_REQUEST_OPTIONS.ignoreCache;
        const headers = options.headers || DEFAULT_REQUEST_OPTIONS.headers;
        const timeout = options.timeout || DEFAULT_REQUEST_OPTIONS.timeout;

        return new Promise<RequestResult>((resolve, reject) => {
            const xhr = new XMLHttpRequest();
            xhr.open(method, this.withQuery(url, queryParams)); // open sync

            if (headers) {
                Object.keys(headers).forEach(key => xhr.setRequestHeader(key, headers[key]));
            }

            if (ignoreCache) {
                xhr.setRequestHeader('Cache-Control', 'no-cache');
            }

            xhr.timeout = timeout || 5000;

            xhr.onload = evt => {
                resolve(this.parseXHRResult(xhr));
            };

            xhr.onerror = evt => {
                reject(this.errorResponse(xhr, 'Failed to make request.'));
            };

            xhr.ontimeout = evt => {
                reject(this.errorResponse(xhr, 'Request took longer than expected.'));
            };

            if (method === 'post' && body) {
                xhr.setRequestHeader('Content-Type', 'application/json');
                xhr.send(JSON.stringify(body));
            } else {
                xhr.send();
            }
        });
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private queryParams(params: any = {}): string {
        return Object.keys(params)
            .map(k => encodeURIComponent(k) + '=' + encodeURIComponent(params[k]))
            .join('&');
    }

    private withQuery(url: string, params: any = {}): string {
        const queryString = this.queryParams(params);
        return queryString ? url + (url.indexOf('?') === -1 ? '?' : '&') + queryString : url;
    }

    private parseXHRResult(xhr: XMLHttpRequest): RequestResult {
        return {
            ok: xhr.status >= 200 && xhr.status < 300,
            status: xhr.status,
            statusText: xhr.statusText,
            headers: xhr.getAllResponseHeaders(),
            data: xhr.responseText,
            json: <T>() => {
                try {
                    return JSON.parse(xhr.responseText) as T;
                } catch (err) {
                    return {} as T;
                }
            },
        };
    }

    private errorResponse(xhr: XMLHttpRequest, message: string | null = null): RequestResult {
        return {
            ok: false,
            status: xhr.status,
            statusText: xhr.statusText,
            headers: xhr.getAllResponseHeaders(),
            data: message || xhr.statusText,
            json: <T>() => {
                try {
                    return JSON.parse(message || xhr.statusText) as T
                } catch (err) {
                    return {} as T;
                }
            },
        };
    }


}
