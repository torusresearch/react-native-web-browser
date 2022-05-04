"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.WebBrowserResultType = exports.WebBrowserPresentationStyle = exports.UnavailabilityError = void 0;

/**
 * If there is no native AuthSession implementation available (which is the case on Android) the params inherited from
 * [`WebBrowserOpenOptions`](#webbrowseropenoptions) will be used in the browser polyfill. Otherwise, the browser parameters will be ignored.
 */
let WebBrowserResultType;
/**
 * A browser presentation style. Its values are directly mapped to the [`UIModalPresentationStyle`](https://developer.apple.com/documentation/uikit/uiviewcontroller/1621355-modalpresentationstyle).
 *
 * @platform ios
 */

exports.WebBrowserResultType = WebBrowserResultType;

(function (WebBrowserResultType) {
  WebBrowserResultType["CANCEL"] = "cancel";
  WebBrowserResultType["DISMISS"] = "dismiss";
  WebBrowserResultType["OPENED"] = "opened";
  WebBrowserResultType["LOCKED"] = "locked";
})(WebBrowserResultType || (exports.WebBrowserResultType = WebBrowserResultType = {}));

let WebBrowserPresentationStyle;
exports.WebBrowserPresentationStyle = WebBrowserPresentationStyle;

(function (WebBrowserPresentationStyle) {
  WebBrowserPresentationStyle["FULL_SCREEN"] = "fullScreen";
  WebBrowserPresentationStyle["PAGE_SHEET"] = "pageSheet";
  WebBrowserPresentationStyle["FORM_SHEET"] = "formSheet";
  WebBrowserPresentationStyle["CURRENT_CONTEXT"] = "currentContext";
  WebBrowserPresentationStyle["OVER_FULL_SCREEN"] = "overFullScreen";
  WebBrowserPresentationStyle["OVER_CURRENT_CONTEXT"] = "overCurrentContext";
  WebBrowserPresentationStyle["POPOVER"] = "popover";
  WebBrowserPresentationStyle["AUTOMATIC"] = "automatic";
})(WebBrowserPresentationStyle || (exports.WebBrowserPresentationStyle = WebBrowserPresentationStyle = {}));

class UnavailabilityError extends Error {
  constructor(tag, methodName) {
    super(`${UnavailabilityError.name}: ${tag}: ${methodName}`);
  }

}

exports.UnavailabilityError = UnavailabilityError;
//# sourceMappingURL=WebBrowser.types.js.map