import CommonUtils from './CommonUtils'
import CookieUtils from './CookieUtils'

// Classe contenant des méthodes utilitaires pour l'upload de fichier
class UploadUtils {
  // Convertis une url Base64 en Blob
  dataUrltoBlob (dataurl, name) {
    // Convert base64 to raw binary data held in a string
    var byteString = window.atob(dataurl.split(',')[1])

    // Separate out the mime component
    var mimeString = dataurl.split(',')[0].split(':')[1].split(';')[0]

    // Write the bytes of the string to an ArrayBuffer
    var ab = new ArrayBuffer(byteString.length)

    // Create a view into the buffer
    var ia = new Uint8Array(ab)

    // Set the bytes of the buffer to the correct values
    for (var i = 0; i < byteString.length; i++) {
      ia[i] = byteString.charCodeAt(i)
    }

    // Write the ArrayBuffer to a blob, and you're done
    var blob = new Blob([ab], { type: mimeString })
    blob.name = name
    return blob
  }

  // Convertis une image au format Jpeg
  convertImageToJpeg (file) {
    return new Promise((resolve) => {
      var img = new Image()
      img.onload = () => {
        URL.revokeObjectURL(img.src)
        var canvas = document.createElement('canvas')
        var ctx = canvas.getContext('2d')
        canvas.width = img.width
        canvas.height = img.height
        ctx.drawImage(img, 0, 0)
        canvas.toBlob((blob) => {
          resolve(new File([blob], file.name.substr(0, file.name.lastIndexOf('.')) + '.jpg', { type: 'image/jpeg' }))
        }, 'image/jpeg', 0.9)
      }
      img.src = URL.createObjectURL(file)
    })
  }

  // Retourne les dimensions d'une image
  getImageDimension (file) {
    return new Promise((resolve) => {
      var img = new Image()
      img.onload = () => {
        URL.revokeObjectURL(img.src)
        resolve({
          width: img.width,
          height: img.height
        })
      }
      img.src = URL.createObjectURL(file)
    })
  }

  // Recadre une image selon des dimansion données
  cropImage (file, width, height, centerCrop) {
    return new Promise((resolve) => {
      const img = new Image()
      img.onload = () => {
        URL.revokeObjectURL(img.src)
        const imgWidth = img.width
        const imgHeight = img.height

        const dimensions = calculateAspectRatioFit(imgWidth, imgHeight, width, height, centerCrop)
        const canvas = document.createElement('canvas')
        canvas.width = Math.min(dimensions.width, width)
        canvas.height = Math.min(dimensions.height, height)

        const ctx = canvas.getContext('2d')
        ctx.drawImage(img, Math.min(0, -dimensions.marginX / 2), Math.min(0, -dimensions.marginY / 2),
          dimensions.width, dimensions.height)
        canvas.toBlob((blob) => {
          resolve(new File([blob], file.name, { type: file.type }))
        }, file.type, 0.9)
      }

      img.src = URL.createObjectURL(file)
    })
  }

  // Upload un fichier avec gestion de la progression
  upload (url, data, callBackSuccess, callBackError, callBackProgress, callBackAbord) {
    const req = new XMLHttpRequest()
    req.addEventListener('load', () => {
      if (req.readyState === 4) {
        if (req.status === 200 || req.status === 201) {
          if (callBackSuccess) {
            const headers = getResponseHeaders(req)
            callBackSuccess(req.response ? JSON.parse(req.response) : null, headers)
          }
        } else {
          if (callBackError) {
            const json = req.response ? JSON.parse(req.response) : null
            const resp = Object.assign({ status: req.status }, json)
            callBackError(resp)
          }
        }
      }
    })
    if (callBackError) {
      req.addEventListener('error', () => {
        const json = req.response ? JSON.parse(req.response) : null
        const resp = Object.assign({ status: req.status }, json)
        callBackError(resp)
      })
    }
    if (req.upload && callBackProgress) {
      req.upload.addEventListener('progress', callBackProgress)
    }
    if (callBackAbord) {
      req.addEventListener('abort', callBackProgress)
    }

    const formData = new FormData()
    Object.keys(data).forEach(key => {
      const value = data[key]
      if (CommonUtils.isFile(value)) {
        formData.append(key, value, value.name)
      } else {
        formData.append(key, value)
      }
    })
    req.open('POST', process.env.VUE_APP_BACK_BASE_URL + url)
    req.setRequestHeader('X-CSRF-TOKEN', CookieUtils.getCookie('CSRF-TOKEN'))
    req.send(formData)

    return req
  }

  // Retourne les classes CSS pour l'icône d'un fichier à partir de son nom
  getCssFileFromName (fileName) {
    var cssClassType = 'mdi mdi-file mdi-dark mdi-lg'
    if (fileName) {
      var fext = fileName.substr(fileName.lastIndexOf('.') + 1).trim()

      var imageExt = ['jpg', 'jpeg', 'png', 'bmp', 'tif', 'svg', 'gif']
      var audioExt = ['wav', 'mp3', 'fla', 'flac', 'ra', 'rma', 'aif', 'aiff', 'aa', 'aac', 'aax', 'ac3', 'au', 'ogg', 'avr', '3ga', 'flac', 'mid', 'midi', 'm4a', 'mp4a', 'amz', 'mka', 'asx', 'pcm', 'm3u', 'wma', 'xwma']
      var videoExt = ['avi', 'mpg', 'mp4', 'mkv', 'mov', 'wmv', 'vp6', '264', 'vid', 'rv', 'webm', 'swf', 'h264', 'flv', 'mk3d', 'gifv', 'oggv', '3gp', 'm4v', 'movie', 'divx']

      if (fext && fext.length > 2) {
        if (imageExt.indexOf(fext) > -1) {
          cssClassType = 'mdi mdi-file-image mdi-dark mdi-lg'
        } else if (videoExt.indexOf(fext) > -1) {
          cssClassType = 'mdi mdi-file-video mdi-dark mdi-lg'
        } else if (audioExt.indexOf(fext) > -1) {
          cssClassType = 'mdi mdi-file-music mdi-dark mdi-lg'
        } else if (fext === 'pdf') {
          cssClassType = 'mdi mdi-file-pdf mdi-dark mdi-lg'
        } else if (fext === 'odt' || fext === 'doc' || fext === 'docx') {
          cssClassType = 'mdi mdi-file-word mdi-dark mdi-lg'
        } else if (fext === 'ods' || fext === 'xls' || fext === 'xlsx') {
          cssClassType = 'mdi mdi-file-excel mdi-dark mdi-lg'
        } else if (fext === 'odp' || fext === 'ppt' || fext === 'pptx') {
          cssClassType = 'mdi mdi-file-powerpoint mdi-dark mdi-lg'
        } else if (fext === 'txt') {
          cssClassType = 'mdi mdi-file-document mdi-dark mdi-lg'
        }
      }
    }
    return cssClassType
  }

  // Retourne les classes CSS pour l'icône d'un fichier à partir de son type ou sinon de son nom
  getCssFileFromType (fileType, fileName) {
    var cssClassType
    if (fileType) {
      var subFiletype = fileType.substring(0, 5)
      if (subFiletype === 'image' || subFiletype === 'video') {
        cssClassType = 'mdi mdi-file-' + subFiletype + ' mdi-dark mdi-lg'
      } else if (subFiletype === 'audio') {
        cssClassType = 'mdi mdi-file-music mdi-dark mdi-lg'
      } else if (fileType === 'application/pdf') {
        cssClassType = 'mdi mdi-file-pdf mdi-dark mdi-lg'
      }
    }
    if (!cssClassType) {
      cssClassType = this.getCssFileFromName(fileName)
    }
    return cssClassType
  }

  // Retourne l'url d'un fichier interne
  getInternalUrl (url) {
    if (url) {
      return url.startsWith('https:') || url.startsWith('http:') || url.startsWith('ftp:') ? url : process.env.VUE_APP_BACK_BASE_URL + url
    }
    return url
  }
}

// Retourne une map des headers d'une XMLHttpRequest
function getResponseHeaders (response) {
  // Get the raw header string
  var headers = response.getAllResponseHeaders()

  // Convert the header string into an array
  // of individual headers
  var arr = headers.trim().split(/[\r\n]+/)

  // Create a map of header names to values
  var headerMap = {}
  arr.forEach(line => {
    var parts = line.split(': ')
    var header = parts.shift()
    var value = parts.join(': ')
    headerMap[header] = value
  })
  return headerMap
}

// Calcul le ratio d'un cadre dans une image
function calculateAspectRatioFit (srcWidth, srcHeight, maxWidth, maxHeight, centerCrop) {
  const ratio = centerCrop ? Math.max(maxWidth / srcWidth, maxHeight / srcHeight)
    : Math.min(maxWidth / srcWidth, maxHeight / srcHeight)
  return {
    width: srcWidth * ratio,
    height: srcHeight * ratio,
    marginX: srcWidth * ratio - maxWidth,
    marginY: srcHeight * ratio - maxHeight
  }
}

export default new UploadUtils()
