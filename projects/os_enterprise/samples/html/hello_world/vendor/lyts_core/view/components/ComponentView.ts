import style from "./ComponentStyle";

export default function view(uid: string, props?: any): string {

    return `
            ${style(uid, props)}
            <div id="${uid}">
                ${ !!props ? props.test : "" }
            </div>
        `;

}
