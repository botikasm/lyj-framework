import style from "./style";

export default function view(uid: string, props?: any): string {
    props = props || {};
    return `
            <div id="${uid}">
                ${ style(uid, props) }
                
                <!-- container -->
                <div class="${uid}_cont">
                    
                    <!-- header -->
                    <div class="${uid}_header">       
                        <div class="${uid}_header__logo">                 
                            <img id="${uid}_fld_logo" src="build/assets/images/logo.png" alt data-i18n="logo"/>
                        </div>                                                                 
                        <label data-i18n="lbl_login"></label>
                    </div>                    
                    
                    <!-- body -->
                    <form class="${uid}_body">
                                     
                        <!-- email field -->                                   
                        <div class="input-field">
                            <input id="${uid}_fld_email" type="text" placeholder data-i18n="insert_your_email_ph">
                            <label for="${uid}_fld_email" data-i18n="lbl_email"></label>
                        </div>
                        
                        <!-- password field -->
                        <div class="input-field">
                            <input id="${uid}_fld_password" type="password" placeholder data-i18n="insert_your_password_ph">
                            <label for="${uid}_fld_password" data-i18n="lbl_password"></label>
                        </div>
                               
                        <!-- forgot passsword -->
                        <div class="${uid}_signin-body__forgot_pwd">
                            <label id="${uid}_btn_forgot_password"  data-i18n="btn_forgot_password"></label>
                        </div>                               
                                                                                               
                        <!-- enter button -->
                        <div class="${uid}_body__enter" >                            
                            <a id="${uid}_btn_signin" class="waves-effect waves-light btn-large" data-i18n="btn_enter"></a>                                                                                
                        </div>
                        
                        <!-- register -->
                        <div class="${uid}_body__register">
                            <label data-i18n="not_yet_assembler_account"></label>  
                            <label id="${uid}_btn_register" data-i18n="btn_register"></label>                      
                        </div>
                        
                                                                                                                    
                    </form>
                                    
                </div>                                
                                                                                                                           
            </div>

        `;
}