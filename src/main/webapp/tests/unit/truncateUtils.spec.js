
import TruncateUtils from '@/services/util/TruncateUtils.js'

describe('TruncateUtils.js tests', () => {
  it('test 1 TruncateUtils - characters withouy breakOnWord', () => {
    var result = TruncateUtils.characters('Phrase de test', 8, false)
    var expectResult = 'Phrase...'
    expect(result).toStrictEqual(expectResult)
  })

  it('test 2 TruncateUtils - characters with breakOnWord', () => {
    var result = TruncateUtils.characters('Phrase de test', 8, true)
    var expectResult = 'Phrase d...'
    expect(result).toStrictEqual(expectResult)
  })

  it('test 3 TruncateUtils - characters no truncate', () => {
    var result = TruncateUtils.characters('Phrase', 8, true)
    var expectResult = 'Phrase'
    expect(result).toStrictEqual(expectResult)
  })

  it('test 4 TruncateUtils - words', () => {
    var result = TruncateUtils.words('Phrase de test', 2)
    var expectResult = 'Phrase de...'
    expect(result).toStrictEqual(expectResult)
  })

  it('test 5 TruncateUtils - words no truncate', () => {
    var result = TruncateUtils.words('Phrase de test', 8, true)
    var expectResult = 'Phrase de test'
    expect(result).toStrictEqual(expectResult)
  })
})
