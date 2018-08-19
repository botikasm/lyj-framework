import ElementWrapper from "../../../../../../vendor/lyts_core/view/components/ElementWrapper";
import {Route} from "../../../../../../vendor/lyts_core/view/Router";
import view from "./view";
import Page from "../../../../../../vendor/lyts_core/view/pages/page/Page";
import ly from "../../../../../../vendor/lyts_core/ly";
import console from "../../../../../../vendor/lyts_core/commons/console";
import globals from "../../../../../globals";
import ToastController from "../../../../../controllers/ToastController";
import constants from "../../../../../constants";


export default class Page1
    extends Page {


    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private readonly _btn_connect: ElementWrapper;

    private readonly _lbl_version: ElementWrapper;
    private readonly _lbl_device: ElementWrapper;

    private readonly _fld_requestType: ElementWrapper;
    private readonly _fld_recipient: ElementWrapper;
    private readonly _fld_request: ElementWrapper;
    private readonly _fld_value: ElementWrapper;
    private readonly _fld_index: ElementWrapper;
    private readonly _fld_response_endpoint: ElementWrapper;
    private readonly _fld_response_byte_count: ElementWrapper;
    private readonly _btn_send: ElementWrapper;

    private readonly _usb_response: ElementWrapper;

    private _device: any; // selected USB Device

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    constructor(route: Route) {
        super(route);

        this._btn_connect = super.getFirst("#" + this.uid + "_btn_connect");
        this._btn_send = super.getFirst("#" + this.uid + "_btn_send");

        this._fld_requestType = super.getFirst("#" + this.uid + "_fld_requestType");
        this._fld_recipient = super.getFirst("#" + this.uid + "_fld_recipient");
        this._fld_request = super.getFirst("#" + this.uid + "_fld_request");
        this._fld_value = super.getFirst("#" + this.uid + "_fld_value");
        this._fld_index = super.getFirst("#" + this.uid + "_fld_index");
        this._fld_response_endpoint = super.getFirst("#" + this.uid + "_fld_response_endpoint");
        this._fld_response_byte_count = super.getFirst("#" + this.uid + "_fld_response_byte_count");

        this._usb_response = super.getFirst("#" + this.uid + "_usb_response");

        this._lbl_version = super.getFirst("#version");
        this._lbl_device = super.getFirst("#" + this.uid + "_device");
    }

    // ------------------------------------------------------------------------
    //                      o v e r r i d e
    // ------------------------------------------------------------------------

    protected render(): string {
        return view(this.uid, {});
    }

    protected free(): void {
        // release memory
        this._btn_connect.removeEventListener();

        console.log("REMOVED:", this.uid);
    }

    protected ready(): void {
        this.init();
    }

    public show(): void {
        super.show();

    }

    public hide(): void {
        super.hide();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private init(): void {

        try {
            this._lbl_version.innerHTML = constants.version;

            // call debounced function
            this._btn_connect.addEventListener('click', ly.lang.funcDebounce(this, this.onButtonClick, 1000, true, 'param1'));
            this._btn_send.addEventListener('click', ly.lang.funcDebounce(this, this.onBtnSendClick, 1000, true));
        } catch (err) {
            console.error("Page1.init()", err)
        }
    }

    private onButtonClick(ev: Event, param1: string): void {
        ev.preventDefault();
        const self: Page1 = this;
        ly.dom.ready(function () {
            self.initUSB();
        }, this);
    }

    private onBtnSendClick(ev: Event, param1: string): void {
        ev.preventDefault();
        const self: Page1 = this;
        ly.dom.ready(function () {
            self.sendMessage();
        }, this);
    }


    private setLblDevice(): void {
        if (!!this._lbl_device && !!this._device) {
            const device = this._device;
            this._lbl_device.innerHTML = "CONNECTED: " + device.productName + " - " + device.manufacturerName;
            console.log('CONFIGURATIONS: ', device.configurations);
        }
    }

    private initUSB(): void {
        console.log('', 'Getting USB Devices...');
        try {
            const navigator: any = globals.root.navigator;
            const array_filters: any = []; //[{vendorId: 0x8457}];

            const usb: any = !!navigator.usb ? navigator.usb : navigator.USB;


            // get coupled devices
            usb.getDevices()
                .then((devices: any) => {
                    devices.map((device: any) => {
                        console.log('Page1.initUSB', device);
                        console.log('Page1.initUSB', device.productName);
                        console.log('Page1.initUSB', device.manufacturerName);
                    });
                });

            /**  **/
            usb
                .requestDevice({filters: array_filters})
                .then((device: any) => {
                    this._device = device;
                    return device.open();
                })
                .then(() => {
                    this.setLblDevice();
                })
                .catch((error: any) => {
                    console.error('Page1.initUSB', error);
                    ToastController.instance().showError(error);
                });

        } catch (err) {
            console.error('Page1.initUSB', err);
        }
    }

    private sendMessage(): void {
        try {
            if (!!this._device) {
                const device = this._device;
                // request
                const requestType: string = this._fld_requestType.value();
                const recipient: string = this._fld_recipient.value();
                const request: any = this._fld_request.value();
                const value: any = this._fld_request.value();
                const index: any = this._fld_request.value();
                // response
                const response_endpoint: any = this._fld_response_endpoint.value();
                const response_byte_count: any = this._fld_response_byte_count.value();

                if (!!requestType && !!recipient) {
                    this.sendMessageToUSB(device, requestType, recipient, request, value, index,
                        response_endpoint, response_byte_count);
                } else {
                    ToastController.instance().showError("Channel and Message are required fields.");
                }
            } else {
                ToastController.instance().showError("DEVICE NOT CONNECTED");
            }
        } catch (err) {
            console.error('Page1.sendMessage', err);
        }
    }

    /**
     * Send a message to USB device.
     */
    private sendMessageToUSB(device: any,
                             requestType: string,
                             recipient: string,
                             request: any,
                             value: any,
                             index: any,
                             response_endpoint: any,
                             response_byte_count: any): void {
        try {
            device.selectConfiguration(1)
                .then(() => {
                    return device.claimInterface(index)
                })
                .then(() => {
                    return device.controlTransferOut({
                        requestType: 'class',
                        recipient: 'interface',
                        request: request, //0x22,
                        value: value, // 0x01,
                        index: index, //0x02
                    })
                })
                // Waiting for 64 bytes of data from endpoint #5.
                .then(() => {
                    device.transferIn(response_endpoint, response_byte_count)
                        .then((result: any) => {
                            if (!!result) {
                                const decoder = new TextDecoder();
                                const decoded_data: string = decoder.decode(result.data);

                                this._usb_response.value(decoded_data);

                                console.log('Received: ' + decoded_data);
                            } else {
                                // ToastController.instance().showInfo("NO DATA INTO RESPONSE");
                                this._usb_response.value("NO DATA INTO RESPONSE");
                            }
                        })
                        // get errors
                        .catch((error: any) => {
                            this.logError('Page1.sendMessageToUSB#transferIn', error);
                        });
                })
                // get errors
                .catch((error: any) => {
                    this.logError('Page1.sendMessageToUSB', error);
                });
        } catch (err) {
            this.logError('Page1.sendMessageToUSB', err);
        }
    }

    private logError(scope:string, err:any):void{
        console.error(scope, err);
        ToastController.instance().showError(err);
    }


}