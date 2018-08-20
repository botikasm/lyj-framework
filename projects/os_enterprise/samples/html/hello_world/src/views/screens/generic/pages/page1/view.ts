import style from "./style";

export default function view(uid: string, props?: any): string {
    props = props || {};
    return `
            <div id="${uid}" class="container">
                ${ style(uid, props) }
   
                <h1>TEST PAGE</h1>
                <span id="version">0.0.0</span>
                
                <div class="section">
                    <h5>1. Register Accounts</h5>
                    <div class="row">
                        
                        <form class="col s12">
                            <div class="row">
                                <!-- register-username -->
                                <div class="input-field col s4">
                                  <input id="${uid}_fld_register_username" type="text" class="validate" value="">
                                  <label for="${uid}_fld_register_username">Username</label>
                                </div>
                                
                                <!-- register-password -->
                                <div class="input-field col s8">
                                  <input id="${uid}_fld_register_password" type="text" class="validate" value="">
                                  <label for="${uid}_fld_register_password">Password</label>
                                </div>
                            </div>
                        </form>
                        
                    </div>
                    
                    <div class="row">
                       <a id="${uid}_btn_register" class="waves-effect waves-light btn">
                       <i class="material-icons right">send</i>Register
                       </a>  
                    </div>
                    
                 </div>
                
                <div class="section">
                    <h5>2. Test Login</h5>
                    <div class="row">
                        <form class="col s12">
                            <div class="row">
                                <!-- login-username -->
                                <div class="input-field col s4">
                                  <input id="${uid}_fld_login_username" type="text" class="validate" value="">
                                  <label for="${uid}_fld_login_username">Username</label>
                                </div>
                                
                                <!-- login-password -->
                                <div class="input-field col s8">
                                  <input id="${uid}_fld_login_password" type="text" class="validate" value="">
                                  <label for="${uid}_fld_login_password">Password</label>
                                </div>
                            </div>
                        </form>
                        <div class="row">
                            <a id="${uid}_btn_login" class="waves-effect waves-light btn">
                            <i class="material-icons right">send</i>Login
                            </a>  
                        </div>
                    
                    </div>
                </div>    
                
                <div class="section">
                    <h5>3. User's List</h5>
                    <div class="row">
                    
                        <div id="${uid}_user_list" class="col s12"> </div>
                        
                    </div>
                </div>  
                     
            </div>

        `;
}