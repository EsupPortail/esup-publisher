module.exports = {
  preset: '@vue/cli-plugin-unit-jest',
  transform: {
    '^.+\\.vue$': 'vue-jest'
  },
  moduleNameMapper: {
    '^@/(.*)$': '<rootDir>/src/main/webapp/src/$1'
  }
}
