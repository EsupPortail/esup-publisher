import DateUtils from '@/services/util/DateUtils.js'

// Tests unitaires sur la classe utilitaire DateUtils
describe('DateUtils.js tests', () => {
  it('test 1 DateUtils - convertDateTimeFromServer function OK', () => {
    var result = DateUtils.convertDateTimeFromServer('09/12/2021')
    var expectResult = new Date('09/12/2021')
    expect(result).toStrictEqual(expectResult)
  })

  it('test 2 DateUtils - convertDateTimeFromServer function KO: no date provided', () => {
    var result = DateUtils.convertDateTimeFromServer()
    expect(result).toBe(null)
  })

  it('test 3 DateUtils - convertLocalDateFromServer function OK', () => {
    var result = DateUtils.convertLocalDateFromServer('2021-12-09')
    var expectResult = new Date('2021', '11', '09')
    expect(result).toStrictEqual(expectResult)
  })

  it('test 4 DateUtils - convertLocalDateFromServer function KO: no date provided', () => {
    var result = DateUtils.convertLocalDateFromServer()
    expect(result).toBe(null)
  })

  it('test 5 DateUtils - convertLocalDateToServer function OK', () => {
    var result = DateUtils.convertLocalDateToServer('2021-12-09')
    var expectResult = '2021-12-09'
    expect(result).toStrictEqual(expectResult)
  })

  it('test 6 DateUtils - convertLocalDateToServer function KO: no date provided', () => {
    var result = DateUtils.convertLocalDateToServer()
    expect(result).toBe(null)
  })

  it('test 7 DateUtils - addDaysToLocalDate function OK', () => {
    var result = DateUtils.addDaysToLocalDate(new Date('2021', '11', '09'), 1)
    var expectResult = new Date('2021', '11', '10')
    expect(result).toStrictEqual(expectResult)
  })

  it('test 8 DateUtils - addDaysToLocalDate function KO: no date provided', () => {
    var result = DateUtils.addDaysToLocalDate()
    expect(result).toBe(null)
  })

  it('test 9 DateUtils - addDaysToLocalDate function KO: no nbDays provided', () => {
    var result = DateUtils.addDaysToLocalDate(new Date('2021', '11', '09'))
    expect(result).toStrictEqual(null)
  })

  it('test 10 DateUtils - addDaysToLocalDate function KO: no valid nbDays format provided', () => {
    var result = DateUtils.addDaysToLocalDate(new Date('2021', '11', '09'), '1')
    expect(result).toStrictEqual(null)
  })

  it('test 11 DateUtils - addDaysToLocalDate function KO: no valid date format provided', () => {
    var result = DateUtils.addDaysToLocalDate('2021-12-09', 1)
    expect(result).toStrictEqual(null)
  })

  it('test 12 DateUtils - convertToIntString function OK', () => {
    var result = DateUtils.convertToIntString(new Date(2021, 11, 9, 10, 55, 40), {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: 'numeric',
      minute: 'numeric',
      second: 'numeric'
    }, 'en')
    expect(result).toStrictEqual('December 9, 2021, 10:55:40 AM')
  })

  it('test 13 DateUtils - min function OK', () => {
    var date1 = DateUtils.convertDateTimeFromServer('09/12/2021')
    var date2 = DateUtils.convertDateTimeFromServer('09/12/2022')
    var result = DateUtils.min(date1, date2)
    expect(result).toStrictEqual(date1)

    result = DateUtils.min(date1, null)
    expect(result).toStrictEqual(date1)

    result = DateUtils.min(null, date2)
    expect(result).toStrictEqual(date2)

    result = DateUtils.min(null, null)
    expect(result).toStrictEqual(null)
  })
})
