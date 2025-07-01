module.exports = {
  moduleNameMapper: {
    '\\.(css|less|scss|sass)$': 'identity-obj-proxy',
  },
  testEnvironment: 'jsdom',
  transform: {
    "^.+\\.[jt]sx?$": "babel-jest"
  },
  testMatch: [
    "<rootDir>/src/**/*.test.[jt]s?(x)"
  ],
  testPathIgnorePatterns: [
    "/node_modules/",
    "/e2e/",
    "/e2e-temp/",
    "\\.spec\\.[jt]s$"
  ],
};
