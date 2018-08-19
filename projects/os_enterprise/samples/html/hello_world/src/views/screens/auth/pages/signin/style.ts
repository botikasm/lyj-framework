export default function style(uid: string, props?: any): string {

    // main
    const main: string = `
        <style>
            
            #${uid}{                                  
                margin: 5rem auto;      
            }
            
            .${uid}_cont{
                padding: 2rem;
                border: 1px solid lightgray;    
                border-radius: .3rem;                                                                    
            }
            
            .${uid}_header{
                display: flex;
                justify-content: space-between;                             
                border-bottom: 1px solid lightgray;
                padding-bottom: .8rem;
                margin-bottom: 3rem;
            }
            
            .${uid}_header__logo{
                display: flex;                         
            }
            
            .${uid}_header__logo i{
                margin-left: 0.8rem;
                align-self: flex-end;     
            }
            
            .${uid}_header img{
                height: 3.2rem;
                width: auto;
                cursor: pointer;
            }
            
            .${uid}_header label{
                align-self: flex-end;
                font-size: 0.9rem;                
            }
            
            .${uid}_body{
                padding: 0 .8rem;
            }
            
            .${uid}_body .input-field{
                margin-top: 2.5rem;
            }
            
                        
            #${uid}_fld_forgot_password{
                border-bottom: 1px dotted currentColor;
                font-size: 0.9rem;  
            }
            
            #${uid}_fld_forgot_password:hover,
            #${uid}_fld_forgot_password:active{
                cursor: pointer;                
            }
            
            .${uid}_body__enter{   
                margin-top: 1.5rem;                             
                display: flex;                
            }
            
            .${uid}_body__enter a{                
            }
                                    
            .${uid}_body__register{
                margin-top: 1.5rem;
                display: flex;  
                align-items: flex-end;
            }
                 
            #${uid}_fld_register{
                margin-left: 0.8rem;
                border-bottom: 1px dotted currentColor;
                font-size: 0.9rem;  
            }
            
            #${uid}_fld_register:hover,
            #${uid}_fld_register:active{
                cursor: pointer;                
            }     
                                    
        </style>      
    `;

    const ex_large: string = `
        <style>
            @media only screen and (min-width: 1201px) {
                #${uid}{    
                    width: 50%;     
                }                                           
            }
        </style>        
    `;

    const large: string = `
        <style>
            @media only screen and (min-width: 993px) and (max-width: 1200px) {
                #${uid}{    
                    width: 75%;     
                }                             
            }
        </style>        
    `;

    const medium: string = `
        <style>
            @media only screen and (min-width: 601px) and (max-width: 992px) {
                #${uid}{    
                    width: 80%;     
                }            
            }
        </style>        
    `;

    const small: string = `
        <style>
            @media only screen and (min-width: 481px) and (max-width: 600px) {
                #${uid}{    
                    width: 100%;     
                }              
            }
        </style>        
    `;

    const ex_small: string = `
        <style>
            @media only screen and (max-width: 480px) {
                #${uid}{    
                    width: 100%;     
                }                                                                           
            }
        </style>        
    `;


    return `
        ${main}
        ${ex_large}           
        ${large}
        ${medium}
        ${small}
        ${ex_small}
    `;
};