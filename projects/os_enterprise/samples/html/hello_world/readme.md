Configuration
=============

1. **Install Webpack locally (as a project dependency)**
```bash
   npm install --save-dev webpack
```
Installing locally is what we recommend for most projects. 
This makes it easier to upgrade projects individually when breaking changes are introduced. 
[More Info Here](https://webpack.js.org/guides/installation/)

2. **Configure Webpack to use Typescript**

First you need to have Typescript installed

*Install Typescript locally*
```bash
npm install --save-dev typescript
```

*Install Loader*
```bash
   npm install ts-loader --save-dev
```

Let's set up a simple configuration to support JSX and compile TypeScript down to ES5.

*tsconfig.json*
```json
{
  "compilerOptions": {
    "outDir": "./build/",
    "noImplicitAny": true,
    "module": "es6",
    "target": "es5",
    "jsx": "react",
    "allowJs": true
  }
}
```

 *Basic webpack.config.js*
  
```javascript
   const path = require('path');
   
   const config = {
   
       entry: './src/index.ts',
   
       output: {
           path: path.resolve(__dirname, 'build'),
           filename: 'bundle.js'
       },
   
       resolve: {
           // Add '.ts' and '.tsx' as a resolvable extension.
           extensions: [".ts", ".tsx", ".js"]
       },
       
       module: {
           rules: [
               {
                   test: /\.tsx?$/,
                   use: 'ts-loader',
                   exclude: /node_modules/
               }
           ]
       }
   };
   
   module.exports = config;
```

[More Info Here](https://www.typescriptlang.org/docs/handbook/integrating-with-build-tools.html#webpack)

Source Map
----------
To learn more about source maps, see the [development guide](https://webpack.js.org/guides/development).

To enable source maps, we must configure TypeScript to output inline source maps to our compiled JavaScript files. 
The following line must be added to our TypeScript configuration:

*tsconfig.json*
```json
{
  "compilerOptions": {
    "outDir": "./build/",
    "noImplicitAny": true,
    "module": "es6",
    "target": "es5",
    "jsx": "react",
    "allowJs": true,
    "sourceMap": true
  }
}
```

Now we need to tell webpack to extract these source maps and into our final bundle:

*webpack.config.js*
  
```javascript
   const path = require('path');
   
   const config = {
   
       entry: './src/index.ts',
   
       output: {
           path: path.resolve(__dirname, 'build'),
           filename: 'bundle.js'
       },
   
       resolve: {
           // Add '.ts' and '.tsx' as a resolvable extension.
           extensions: [".ts", ".tsx", ".js"]
       },
       
       module: {
           rules: [
               {
                   test: /\.tsx?$/,
                   use: 'ts-loader',
                   exclude: /node_modules/
               }
           ]
       },
       
       devtool: 'inline-source-map'
   };
   
   module.exports = config;
```

See the [devtool documentation](https://webpack.js.org/configuration/devtool/) for more information.