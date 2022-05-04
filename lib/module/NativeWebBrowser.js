import { NativeModules, Platform } from 'react-native';
const LINKING_ERROR = `The package 'react-native-web-browser' doesn't seem to be linked. Make sure: \n\n` + Platform.select({
  ios: "- You have run 'pod install'\n",
  default: ''
}) + '- You rebuilt the app after installing the package\n' + '- You are not using Expo managed workflow\n';
const NativeWebBrowser = NativeModules.NativeWebBrowser ? NativeModules.NativeWebBrowser : new Proxy({}, {
  get() {
    throw new Error(LINKING_ERROR);
  }

});
export default NativeWebBrowser;
//# sourceMappingURL=NativeWebBrowser.js.map