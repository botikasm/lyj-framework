import style from "./style";

export default function view(uid: string, props?: any): string {
    props = props || {};
    return `
            <div id="${uid}">
                ${ style(uid, props) }
   
                <div class="${uid}_cont">
                
                    <!-- header -->
                    <div class="${uid}_header">
                        <div class="${uid}_header__logo">
                            <img id="${uid}_fld_logo" src="build/assets/images/logo.png" alt data-i18n="logo"/>
                        </div>
                        <label data-i18n="lbl_register"></label>
                    </div>   
                    
                     <!-- body -->
                    <form class="${uid}_body">
                    
                        <!-- connection -->
                        <p id="${uid}_connection_box" 
                            class="${uid}_connection"></p> 
                        
                        <!-- email field -->                                   
                        <div class="input-field">
                            <input id="${uid}_fld_email" type="text" placeholder data-i18n="insert_a_valid_email_ph">
                            <label id="${uid}_lbl_email" for="${uid}_fld_email" data-i18n="lbl_email"></label>                                                                                 
                        </div>
                        
                        <!-- password field -->
                        <div class="input-field">
                            <input id="${uid}_fld_password" type="password" placeholder data-i18n="insert_a_password_ph">
                            <label id="${uid}_lbl_password" for="${uid}_fld_password" data-i18n="lbl_password"></label>
                        </div>
                        
                        <!-- repeat password field -->
                        <div class="input-field">
                            <input id="${uid}_fld_repassword" type="password" placeholder data-i18n="repeat_password_ph">
                            <label id="${uid}_lbl_repassword" for="${uid}_fld_repassword" data-i18n="lbl_repassword"></label>
                        </div>
                        
                        <!-- action -->
                        <div class="${uid}_body__enter">                            
                            <a id="${uid}_btn_register" class="waves-effect waves-light btn-large" data-i18n="btn_register"></a>                                                   
                        </div>
                        
                        <!-- go to login -->
                        <div id="${uid}_goto_login_box" class="${uid}_body__goto-login">
                            <label data-i18n="already_assembler_account"></label>  
                            <label id="${uid}_btn_goto_login" data-i18n="btn_goto_login"></label>                      
                        </div>
                        
                        
                    </form>
                    
                </div>

                     
            </div>

        `;
}