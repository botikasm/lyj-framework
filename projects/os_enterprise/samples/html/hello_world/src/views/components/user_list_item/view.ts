import style from "./style";

export default function view(uid: string, props?: any): string {
    props = props || {};
    return `
            <div id="${uid}" class="row z-depth-1">
                ${ style(uid, props) }
            
                <div class="col s5">
                    <span id="${uid}_username"></span>
                </div>
                <div class="col s5">
                    <span id="${uid}_password"></span>
                </div>
                <div class="col s2">
                    <i id="${uid}_btn_remove" class="clickable material-icons">delete</i>
                </div>
                
            </div>

        `;
}