import constants from "../constants";
import AuthenticationService from "./services/AuthenticationService";


const HOST: string = constants.host;
const APP_TOKEN: string = constants.APP_TOKEN;

/**
 * Main services controller.
 * Use this singleton to access all services.
 */
export default class ServiceController {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private _srvc_account: AuthenticationService;


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    private constructor() {
        this.registerServices();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public get account(): AuthenticationService {
        return this._srvc_account;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private registerServices() {

        this._srvc_account = new AuthenticationService(HOST, APP_TOKEN);


    }

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static __instance: ServiceController;

    public static instance(): ServiceController {
        if (null == ServiceController.__instance) {
            ServiceController.__instance = new ServiceController();
        }
        return ServiceController.__instance;
    }

}
