module.exports = {
  preset: '@vue/cli-plugin-unit-jest',
  transform: {
    '^.+\\.vue$': 'vue-jest'
  },
  roots: ['<rootDir>/src/test/javascript/spec/'],
  testMatch: ['<rootDir>/src/test/javascript/spec/**/@(*.)@(spec.js)'],
  moduleNameMapper: {
    '^@/(.*)$': '<rootDir>/src/main/webapp/src/$1'
  }
}
