var PATHS = {
    app: './src/org/lyj/desktopfences/deploy/htdocs/src',
    output: 'src/org/lyj/desktopfences/deploy/htdocs/build'
};

module.exports = {

    entry:  PATHS.app,

    resolve: {
        extensions: ['', '.js', '.jsx']
    },

    output: {
        path:     PATHS.output,
        filename: 'bundle.js',
    },

    module: {
        loaders: [
            {
                test: /\.js?x$/,
                exclude: /(node_modules|bower_components)/,
                //include: "./src/org/lyj/desktopfences/deploy/htdocs/src",
                loader: 'babel',
                query:{
                    presets: ['react', 'es2015']
                }

            }
        ]
    }

};