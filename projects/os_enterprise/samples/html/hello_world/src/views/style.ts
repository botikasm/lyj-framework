export default function style(uid: string, props?: any): string {

    // main
    const main: string = `
        <style>
            .clickable {
                cursor: pointer;
            }
        </style>      
    `;

    return `
        ${main}
           
    `;
};