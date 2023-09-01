module.exports = {
  extends: ['@commitlint/config-conventional'],
  ignores: [
    (message) => message.includes('[maven-release-plugin]')
  ]
}
