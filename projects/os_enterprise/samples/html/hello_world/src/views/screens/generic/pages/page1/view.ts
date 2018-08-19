import style from "./style";

export default function view(uid: string, props?: any): string {
    props = props || {};
    return `
            <div id="${uid}" class="container">
                ${ style(uid, props) }
   
                <h1>USB test page</h1>
                <span id="version">0.0.0</span>
                
                <div class="section">
                    <h5>1. Enable USB</h5>
                    <div class="row">
                        <div class="col s4">
                           <a id="${uid}_btn_connect" class="waves-effect waves-light btn"><i class="material-icons right">settings_power</i>Init USB</a>  
                        </div>
                        <div id="${uid}_devices" class="col s8">
                            <span><b id="${uid}_device">NO DEVICES ARE CONNECTED</b></span>
                        </div>
                    </div>
                    
                 </div>
                
                <div class="section">
                    <h5>2. Set Up Message</h5>
                    <div class="row">
                      <form class="col s12">
                      
                        <div class="row">
                            <div class="input-field col s4">
                              <input id="${uid}_fld_requestType" type="text" class="validate" value="class">
                              <label for="${uid}_fld_requestType">Request Type</label>
                            </div>
                            <div class="input-field col s8">
                              <input id="${uid}_fld_recipient" type="text" class="validate" value="interface">
                              <label for="${uid}_fld_recipient">Recipient</label>
                            </div>
                        </div>
                        
                        <div class="row">
                            <div class="input-field col s3">
                              <input id="${uid}_fld_request" type="text" class="validate" value="0x22">
                              <label for="${uid}_fld_request">Request</label>
                            </div>
                            <div class="input-field col s3">
                              <input id="${uid}_fld_value" type="text" class="validate" value="0x01">
                              <label for="${uid}_fld_value">Value</label>
                            </div>
                            <div class="input-field col s3">
                              <input id="${uid}_fld_index" type="text" class="validate" value="0x00">
                              <label for="${uid}_fld_index">Index</label>
                            </div>
                        </div>
                        
                      
                      </form>
                </div>    
                
                <div class="section">
                    <h5>3. Set Up Response</h5>
                    <form class="col s12">
                    
                         <div class="row">
                            <div class="input-field col s4">
                              <input id="${uid}_fld_response_endpoint" type="text" class="validate" value="5">
                              <label for="${uid}_fld_response_endpoint">End Point #number</label>
                            </div>
                            <div class="input-field col s8">
                              <input id="${uid}_fld_response_byte_count" type="text" class="validate" value="64">
                              <label for="${uid}_fld_response_byte_count">Byte Count (expected bytes in response)</label>
                            </div>
                        </div>
                    
                    </form>
                </div>
                
                <div class="section">
                    <h5>4. Run</h5>
                    <div class="row">
                       <a id="${uid}_btn_send" class="waves-effect waves-light btn"><i class="material-icons right">send</i>Send</a>  
                    </div>
                    <div class="row">
                        <span><b id="${uid}_usb_response">NO RESPONSE FROM USB DEVICE</b></span>
                    </div>
               </div>
                   
               <div class="section">
                    <h5>5. Debug</h5>
                    <div class="row">
                        <div class="col s12">
                           <p>
                           Open <b>chrome://device-log/</b> to view USB Log
                            </p>
                        </div>
                    
                    </div>
                    
               </div>
                     
            </div>

        `;
}