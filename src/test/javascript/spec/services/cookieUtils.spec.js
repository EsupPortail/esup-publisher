import CookieUtils from '@/services/util/CookieUtils.js'

// Tests unitaires sur la classe utilitaire CookieUtils
describe('CookieUtils.js tests', () => {
  it('test 1 CookieUtils - set/get', () => {
    process.env = Object.assign(process.env, { VUE_APP_BACK_BASE_URL: '' })
    const cookieKey = 'unitTest'
    const cookieValue = 'testValue'
    CookieUtils.setCookie(cookieKey, cookieValue)
    expect(CookieUtils.getCookie(cookieKey)).toStrictEqual(cookieValue)
  })
})
