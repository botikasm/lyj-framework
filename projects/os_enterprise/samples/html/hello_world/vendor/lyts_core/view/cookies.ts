import browser from "./browser";

/**
 * Cookies Helper class
 */
export default class cookies {

    public static create(name: string, value: string, days: number): void {
        if (browser.isReady()) {
            let expires = '';
            if (days) {
                let date = new Date();
                date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
                expires = '; expires=' + date.toUTCString();
            }
            else expires = '';
            document.cookie = name + '=' + value + expires + '; path=/';
        }
    }

    public static read(name: string, default_value = ''): string {
        let nameEQ = name + '=';
        let ca = document.cookie.split(';');
        for (let i = 0; i < ca.length; i++) {
            let c = ca[i];
            while (c.charAt(0) == ' ') c = c.substring(1, c.length);
            if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
        }
        return default_value;
    }

    /**
     * Remove a cookie
     * @param name Cookie name
     */
    public static erase(name: string) {
        if (browser.isReady()) {
            cookies.create(name, '', -1);
        }
    }

    /**
     * Remove all cookies
     */
    public static clear(): void {
        if (browser.isReady()) {
            let cookies = document.cookie.split(";");
            for (let i = 0; i < cookies.length; i++) {
                let cookie = cookies[i];
                let eqPos = cookie.indexOf("=");
                let name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
                document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT";
            }
        }
    }
}