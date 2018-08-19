export default function style(uid: string, props?: any): string {

    // SMALL
    const main: string = `
        <style>
        
        #${uid}{
            margin: 0px;
            padding: 0px;
        }
            
        </style>      
    `;

    return `
        ${main}
           
    `;
};