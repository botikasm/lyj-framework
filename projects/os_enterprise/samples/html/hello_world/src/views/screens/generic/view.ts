import style from "./style";

export default function view(uid: string, props?: any): string {
    props = props || {};
    return `
            <div id="${uid}" class="">
                ${ style(uid, props) }
   
                <nav>
                    <div class="nav-wrapper">
                      <a href="#" class="brand-logo">USB TEST</a>
                      <ul id="nav-mobile" class="right hide-on-med-and-down">
                      <!--
                        <li><a data-router="relative" href="auth/signin">Login</a></li>
                      -->
                      </ul>
                    </div>
                </nav>
                
                <div id="${uid}_pages"></div>        
                        
            </div>

        `;
}