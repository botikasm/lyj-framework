export default function style(uid: string, props?: any): string {

    const main: string = `
        <style>
            ${uid} .container {
            
            }
        </style>
    `;

    return `
        ${main}
    `;
};