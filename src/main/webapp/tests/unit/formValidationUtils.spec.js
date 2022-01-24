import { FormValidationUtils, FormErrorType } from '@/services/util/FormValidationUtils.js'

// Tests unitaires sur la classe utilitaire FormValidationUtils
describe('FormValidationUtils.js tests', () => {
  it('test 1 FormValidationUtils - TextField', () => {
    const formValidator = new FormValidationUtils()
    const field = 'name'
    const min = 5
    const max = 10
    const required = true

    var value = null
    formValidator.checkTextFieldValidity(field, value, null, null, false)
    expect(formValidator.hasError(field)).toStrictEqual(false)

    formValidator.checkTextFieldValidity(field, value, min, max, required)
    expect(formValidator.hasError(field, FormErrorType.REQUIRED)).toStrictEqual(true)

    value = 'test'
    formValidator.checkTextFieldValidity(field, value, min, max, required)
    expect(formValidator.hasError(field, FormErrorType.MIN_LENGTH)).toStrictEqual(true)

    value = 'testtesttest'
    formValidator.checkTextFieldValidity(field, value, min, max, required)
    expect(formValidator.hasError(field, FormErrorType.MAX_LENGTH)).toStrictEqual(true)

    value = 'testtest'
    formValidator.checkTextFieldValidity(field, value, min, max, required)
    expect(formValidator.hasError(field)).toStrictEqual(false)
  })

  it('test 2 FormValidationUtils - NumberField', () => {
    const formValidator = new FormValidationUtils()
    const field = 'name'
    const min = 5
    const max = 10
    const required = true

    var value = null
    formValidator.checkNumberFieldValidity(field, value, null, null, false)
    expect(formValidator.hasError(field)).toStrictEqual(false)

    formValidator.checkNumberFieldValidity(field, value, min, max, required)
    expect(formValidator.hasError(field, FormErrorType.REQUIRED)).toStrictEqual(true)

    value = 1
    formValidator.checkNumberFieldValidity(field, value, min, max, required)
    expect(formValidator.hasError(field, FormErrorType.MIN_VALUE)).toStrictEqual(true)

    value = 11
    formValidator.checkNumberFieldValidity(field, value, min, max, required)
    expect(formValidator.hasError(field, FormErrorType.MAX_VALUE)).toStrictEqual(true)

    value = 8
    formValidator.checkNumberFieldValidity(field, value, min, max, required)
    expect(formValidator.hasError(field)).toStrictEqual(false)
  })

  it('test 3 FormValidationUtils - DateField', () => {
    const formValidator = new FormValidationUtils()
    const field = 'name'
    const min = new Date(2022, 1, 1)
    const max = new Date(2022, 1, 10)
    const required = true

    var value = null
    formValidator.checkDateFieldValidity(field, value, null, null, false)
    expect(formValidator.hasError(field)).toStrictEqual(false)

    formValidator.checkDateFieldValidity(field, value, min, max, required)
    expect(formValidator.hasError(field, FormErrorType.REQUIRED)).toStrictEqual(true)

    value = new Date(2022, 0, 1)
    formValidator.checkDateFieldValidity(field, value, min, max, required)
    expect(formValidator.hasError(field, FormErrorType.MIN_DATE)).toStrictEqual(true)

    value = new Date(2022, 1, 15)
    formValidator.checkDateFieldValidity(field, value, min, max, required)
    expect(formValidator.hasError(field, FormErrorType.MAX_DATE)).toStrictEqual(true)

    value = new Date(2022, 1, 8)
    formValidator.checkDateFieldValidity(field, value, min, max, required)
    expect(formValidator.hasError(field)).toStrictEqual(false)
  })

  it('test 4 FormValidationUtils - FileField', () => {
    const formValidator = new FormValidationUtils()
    const field = 'name'
    const pattern = 'image/*'
    const size = 500
    const required = true

    var value = null
    formValidator.checkFileFieldValidity(field, value, null, null, false)
    expect(formValidator.hasError(field)).toStrictEqual(false)

    formValidator.checkFileFieldValidity(field, value, pattern, size, required)
    expect(formValidator.hasError(field, FormErrorType.REQUIRED)).toStrictEqual(true)

    value = { type: 'type', size: 600 }
    formValidator.checkFileFieldValidity(field, value, pattern, size, required)
    expect(formValidator.hasError(field, FormErrorType.PATTERN)).toStrictEqual(true)

    value = { type: 'image/jpeg', size: 600 }
    formValidator.checkFileFieldValidity(field, value, pattern, size, required)
    expect(formValidator.hasError(field, FormErrorType.MAX_SIZE)).toStrictEqual(true)

    value = { type: 'image/jpeg', size: 400 }
    formValidator.checkFileFieldValidity(field, value, pattern, size, required)
    expect(formValidator.hasError(field)).toStrictEqual(false)
  })

  it('test 5 FormValidationUtils - ArrayField', () => {
    const formValidator = new FormValidationUtils()
    const field = 'name'
    const min = 2
    const max = 5
    const required = true

    var value = []
    formValidator.checkArrayFieldValidity(field, value, null, null, false)
    expect(formValidator.hasError(field)).toStrictEqual(false)

    formValidator.checkArrayFieldValidity(field, value, min, max, required)
    expect(formValidator.hasError(field, FormErrorType.REQUIRED)).toStrictEqual(true)

    value = [1]
    formValidator.checkArrayFieldValidity(field, value, min, max, required)
    expect(formValidator.hasError(field, FormErrorType.MIN_VALUE)).toStrictEqual(true)

    value = [1, 2, 3, 4, 5, 6]
    formValidator.checkArrayFieldValidity(field, value, min, max, required)
    expect(formValidator.hasError(field, FormErrorType.MAX_VALUE)).toStrictEqual(true)

    value = [1, 2, 3]
    formValidator.checkArrayFieldValidity(field, value, min, max, required)
    expect(formValidator.hasError(field)).toStrictEqual(false)
  })
})
