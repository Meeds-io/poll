const path = require('path');

const config = {
  context: path.resolve(__dirname, '.'),
  module: {
    rules: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        use: [
          'babel-loader',
          'eslint-loader',
        ]
      },
      {
        test: /\.vue$/,
        use: [
          'vue-loader',
          'eslint-loader',
        ]
      }
    ]
  },
  entry: {
    poll: './src/main/webapp/vue-app/poll/main.js',
    pollExtensions: './src/main/webapp/vue-app/poll-extensions/main.js',
  },
  output: {
    path: path.join(__dirname, 'target/poll/'),
    filename: 'js/[name].bundle.js',
    libraryTarget: 'amd'
  },
  externals: {
    vue: 'Vue',
    vuetify: 'Vuetify',
    jquery: '$',
  },
};

module.exports = config;
