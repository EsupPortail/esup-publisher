import UploadUtils from '@/services/util/UploadUtils.js'
import CommonUtils from '@/services/util/CommonUtils.js'

// Tests unitaires sur la classe utilitaire UploadUtils
describe('UploadUtils.js tests', () => {
  it('test 1 UploadUtils - dataUrltoBlob', () => {
    const imgBase64 = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAC0AAAAbCAYAAADoOQYqAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad5mH3gAAAFJSURBVFhH7ZQxDoJAEEVt7KQzxoLEjtgYWxNKG4/AGbwBJ/DYI87OsrPDBzExOiYWL2E/s7OPAV0sVlv6OWDoHRh6B4begaF3YOgdGGrKK50uLW3W4N63gKHmL/0mYKj5Cel1Q/vLrRPVDKWLY16zrw7Z/WXV0qluaLk6026ijuHBqH7H87BGky1k8660mZYWCRaSLD6oOoylWULtBf1DHeg/JZ4WB9rUoNhIh0OuVOiaPrd1ZgAilKZt18JgUIb+QqaVH9KRNZAH01OOmP344YzkmNyYS6S/eEUavTrZH4XmSMe3McbHpN8y6WekBfq+RFI1xjLDfJb0s89gDL0o+G8sHcTruu0a62mEg7Npm0/jwSzpjnDmi9POgzjZADdnIdtUxBV2WnOlU23eD/7YIzD0Dgy9A0PvwNA7MPQODL0DQ+/A0DVbugNrF9jfuRrGBAAAAABJRU5ErkJggg=='
    expect(CommonUtils.isFile(UploadUtils.dataUrltoBlob(imgBase64, 'test1'))).toBe(true)
  })

  it('test 2 UploadUtils - getCssFileFromName', () => {
    const type = UploadUtils.getCssFileFromName('test.jpg')
    expect(type).toStrictEqual('mdi mdi-file-image mdi-dark mdi-lg')
  })

  it('test 3 UploadUtils - getCssFileFromType', () => {
    const type = UploadUtils.getCssFileFromType('image/jpg', 'test.jpg')
    expect(type).toStrictEqual('mdi mdi-file-image mdi-dark mdi-lg')
  })
})
