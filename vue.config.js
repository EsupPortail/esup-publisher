const { DefinePlugin } = require('webpack')
const fs = require('fs')
const xml2js = require('xml2js')
const path = require('path')
const CKEditorWebpackPlugin = require('@ckeditor/ckeditor5-dev-webpack-plugin')
const { styles } = require('@ckeditor/ckeditor5-dev-utils')

const _outputDir = 'src/main/webapp/dist'

// Recherche de la version de l'application dans le pom.xml
function loadBackVersion () {
  let version
  const pomXml = fs.readFileSync('./pom.xml', 'utf8')
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
  pages: {
    app: {
      entry: 'src/main/webapp/src/main.js',
      template: 'src/main/webapp/public/index.html',
      filename: 'index.html'
    }
  },
  devServer: {
    port: 3000,
    proxy: 'http://localhost:8080'
  },
  publicPath: process.env.VUE_APP_BACK_BASE_URL + 'ui/',
  outputDir: _outputDir,
  productionSourceMap: false,
  parallel: false,
  transpileDependencies: [
    /ckeditor5-[^/\\]+[/\\]src[/\\].+\.js$/
  ],
  configureWebpack: {
    resolve: {
      alias: {
        '@': path.resolve(__dirname, 'src/main/webapp/src')
      }
    },
    plugins: [
      new DefinePlugin({
        'process.env.BACK_VERSION': loadBackVersion(),
        __VUE_I18N_FULL_INSTALL__: true,
        __VUE_I18N_LEGACY_API__: true,
        __VUE_I18N_PROD_DEVTOOLS__: false,
        __INTLIFY_PROD_DEVTOOLS__: false
      }),
      new CKEditorWebpackPlugin({
        language: 'fr',
        additionalLanguages: ['en'],
        translationsOutputFile: /app/
      })
    ]
  },
  chainWebpack: config => {
    config
      .plugin('copy')
      .use(require('copy-webpack-plugin'), [[{
        from: path.resolve(__dirname, 'src/main/webapp/public'),
        to: path.resolve(__dirname, _outputDir),
        toType: 'dir',
        ignore: ['.DS_Store']
      }]])
    // Configuration pour CKEditor
    const svgRule = config.module.rule('svg')
    svgRule.exclude.add(path.join(__dirname, 'node_modules', '@ckeditor'))
    config.module
      .rule('cke-svg')
      .test(/ckeditor5-[^/\\]+[/\\]theme[/\\]icons[/\\][^/\\]+\.svg$/)
      .use('raw-loader')
      .loader('raw-loader')
    config.module
      .rule('cke-css')
      .test(/ckeditor5-[^/\\]+[/\\].+\.css$/)
      .use('postcss-loader')
      .loader('postcss-loader')
      .tap(() => {
        return {
          postcssOptions: styles.getPostCssConfig({
            themeImporter: {
              themePath: require.resolve('@ckeditor/ckeditor5-theme-lark')
            },
            minify: true
          })
        }
      })
    // Configuration pour éviter les warnings sur les webcomponents
    config.module
      .rule('vue')
      .use('vue-loader')
      .tap(options => {
        options.compilerOptions = {
          ...options.compilerOptions,
          isCustomElement: tag => tag.startsWith('esup-')
        }
        return options
      })
  }
}
