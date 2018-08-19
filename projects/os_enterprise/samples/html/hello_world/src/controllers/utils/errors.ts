import ly from "../../../vendor/lyts_core/ly";

export default class errors {

    public static getMessage(error: any): string {
        try {
            let message = '';
            if (!!error) {
                if (ly.lang.isString(error)) {
                    message = error;
                } else if (!!error.message) {
                    message = error.message;
                } else if (!!error.response) {
                    message = error.response;
                }
            }

            return ly.i18n.get(message, message);
        } catch (err) {
            return errors.getMessage(err);
        }
    }

}