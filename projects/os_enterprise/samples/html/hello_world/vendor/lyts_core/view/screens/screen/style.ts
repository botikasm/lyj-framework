export default function style(uid: string, props?: any): string {

    const main: string = `
        <style>
            ${uid} {
            
            }
        </style>
    `;

    return `
        ${main}
    `;
};