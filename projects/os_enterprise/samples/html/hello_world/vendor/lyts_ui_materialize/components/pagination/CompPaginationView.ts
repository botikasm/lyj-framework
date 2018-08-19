import style from "./CompPaginationStyle";


export default function view(uid: string, props?: any): string {
    props = props || {};
    return `                  
          <ul id="${uid}" class="pagination">   
          ${ style(uid, props) }                                  
          </ul>                                                                                               
     `;
}