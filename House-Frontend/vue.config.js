// vue.config.js
module.exports = {
  lintOnSave: false,
  devServer: {
    port: 2019,

    proxy: {
      '/api': {
        target: 'http://www.xzzf.xyz:8080',
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''
        }
      }
    }
  }

}
