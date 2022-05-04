"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;

var _reactNative = require("react-native");

const LINKING_ERROR = `The package 'react-native-web-browser' doesn't seem to be linked. Make sure: \n\n` + _reactNative.Platform.select({
  ios: "- You have run 'pod install'\n",
  default: ''
}) + '- You rebuilt the app after installing the package\n' + '- You are not using Expo managed workflow\n';
const NativeWebBrowser = _reactNative.NativeModules.NativeWebBrowser ? _reactNative.NativeModules.NativeWebBrowser : new Proxy({}, {
  get() {
    throw new Error(LINKING_ERROR);
  }

});
var _default = NativeWebBrowser;
exports.default = _default;
//# sourceMappingURL=NativeWebBrowser.js.map