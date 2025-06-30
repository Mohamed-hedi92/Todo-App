module.exports = {
  moduleNameMapper: {
    '\\.(css|less|scss|sass)$': 'identity-obj-proxy',
  },
  testEnvironment: 'jsdom',
  transform: {
    "^.+\\.[jt]sx?$": "babel-jest"
  },
  testMatch: [
    "**/src/**/*.test.[jt]s?(x)",
    "**/src/**/*.spec.[jt]s?(x)"
  ]
};
