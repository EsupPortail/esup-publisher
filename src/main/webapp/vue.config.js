const { DefinePlugin } = require('webpack')
const fs = require('fs')
const xml2js = require('xml2js')

// Recherche de la version de l'application dans le pom.xml
function loadBackVersion () {
  let version
  const pomXml = fs.readFileSync('../../../pom.xml', 'utf8')
  xml2js.parseString(pomXml, function (err, result) {
    if (err) {
      console.error(err)
    } else {
      version = result.project.version[0]
    }
  })
  return JSON.stringify(version)
}

module.exports = {
  devServer: {
    port: 3000,
    proxy: 'http://localhost:8080'
  },
  publicPath: '/publisher/ui/',
  productionSourceMap: false,
  pluginOptions: {
    i18n: {
      locale: 'fr',
      fallbackLocale: 'en',
      enableInSFC: false,
      enableBridge: false
    }
  },
  configureWebpack: {
    plugins: [
      new DefinePlugin({
        'process.env.BACK_VERSION': loadBackVersion()
      })
    ]
  }
}
