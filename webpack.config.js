const path = require('path');

module.exports = {
	entry: [
		'./src/view/minesweeper.jsx',
		'./style/minesweeper.less'
	],
	output: {
		path: path.join(__dirname, 'view'),
		publicPath: '/',
		filename: 'ms.min.js'
	},
	module: {
		rules: [
			{
				test: /\.jsx?$/,
				exclude: /node_modules/,
				use: {
					loader: 'babel-loader',
					options: {
						presets: [
							['@babel/preset-env', {
								"corejs": 3,
								"useBuiltIns": "usage"
							}],
							'@babel/preset-react',
	    				]
					}
				}
			},
			{
				test: /\.less$/,
				use: [
					"style-loader",
					{
						loader: "css-loader",
						options: {
							url: false
						}
					},
					path.resolve('./src/css-min-loader.js'), // css minifier
					{
						loader: "less-loader",               // less interpreter
						options: {
							lessOptions: {
								javascriptEnabled: true,
							}
						}
					},
				]
			}
		]
	}
}
