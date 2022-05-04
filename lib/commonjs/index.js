"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
Object.defineProperty(exports, "AuthSessionOpenOptions", {
  enumerable: true,
  get: function () {
    return _WebBrowser.AuthSessionOpenOptions;
  }
});
Object.defineProperty(exports, "WebBrowserAuthSessionResult", {
  enumerable: true,
  get: function () {
    return _WebBrowser.WebBrowserAuthSessionResult;
  }
});
Object.defineProperty(exports, "WebBrowserCompleteAuthSessionOptions", {
  enumerable: true,
  get: function () {
    return _WebBrowser.WebBrowserCompleteAuthSessionOptions;
  }
});
Object.defineProperty(exports, "WebBrowserCompleteAuthSessionResult", {
  enumerable: true,
  get: function () {
    return _WebBrowser.WebBrowserCompleteAuthSessionResult;
  }
});
Object.defineProperty(exports, "WebBrowserCoolDownResult", {
  enumerable: true,
  get: function () {
    return _WebBrowser.WebBrowserCoolDownResult;
  }
});
Object.defineProperty(exports, "WebBrowserCustomTabsResults", {
  enumerable: true,
  get: function () {
    return _WebBrowser.WebBrowserCustomTabsResults;
  }
});
Object.defineProperty(exports, "WebBrowserMayInitWithUrlResult", {
  enumerable: true,
  get: function () {
    return _WebBrowser.WebBrowserMayInitWithUrlResult;
  }
});
Object.defineProperty(exports, "WebBrowserOpenOptions", {
  enumerable: true,
  get: function () {
    return _WebBrowser.WebBrowserOpenOptions;
  }
});
Object.defineProperty(exports, "WebBrowserPresentationStyle", {
  enumerable: true,
  get: function () {
    return _WebBrowser.WebBrowserPresentationStyle;
  }
});
Object.defineProperty(exports, "WebBrowserRedirectResult", {
  enumerable: true,
  get: function () {
    return _WebBrowser.WebBrowserRedirectResult;
  }
});
Object.defineProperty(exports, "WebBrowserResult", {
  enumerable: true,
  get: function () {
    return _WebBrowser.WebBrowserResult;
  }
});
Object.defineProperty(exports, "WebBrowserResultType", {
  enumerable: true,
  get: function () {
    return _WebBrowser.WebBrowserResultType;
  }
});
Object.defineProperty(exports, "WebBrowserWarmUpResult", {
  enumerable: true,
  get: function () {
    return _WebBrowser.WebBrowserWarmUpResult;
  }
});
Object.defineProperty(exports, "WebBrowserWindowFeatures", {
  enumerable: true,
  get: function () {
    return _WebBrowser.WebBrowserWindowFeatures;
  }
});
exports.coolDownAsync = coolDownAsync;
exports.dismissAuthSession = dismissAuthSession;
exports.dismissBrowser = dismissBrowser;
exports.getCustomTabsSupportingBrowsersAsync = getCustomTabsSupportingBrowsersAsync;
exports.mayInitWithUrlAsync = mayInitWithUrlAsync;
exports.maybeCompleteAuthSession = maybeCompleteAuthSession;
exports.openAuthSessionAsync = openAuthSessionAsync;
exports.openBrowserAsync = openBrowserAsync;
exports.warmUpAsync = warmUpAsync;

var _reactNative = require("react-native");

var _NativeWebBrowser = _interopRequireDefault(require("./NativeWebBrowser"));

var _WebBrowser = require("./WebBrowser.types");

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

const emptyCustomTabsPackages = {
  defaultBrowserPackage: undefined,
  preferredBrowserPackage: undefined,
  browserPackages: [],
  servicePackages: []
};
/**
 * Returns a list of applications package names supporting Custom Tabs, Custom Tabs
 * service, user chosen and preferred one. This may not be fully reliable, since it uses
 * `PackageManager.getResolvingActivities` under the hood. (For example, some browsers might not be
 * present in browserPackages list once another browser is set to default.)
 *
 * @return The promise which fulfils with [`WebBrowserCustomTabsResults`](#webbrowsercustomtabsresults) object.
 * @platform android
 */

async function getCustomTabsSupportingBrowsersAsync() {
  if (!_NativeWebBrowser.default.getCustomTabsSupportingBrowsersAsync) {
    throw new _WebBrowser.UnavailabilityError('WebBrowser', 'getCustomTabsSupportingBrowsersAsync');
  }

  if (_reactNative.Platform.OS !== 'android') {
    return emptyCustomTabsPackages;
  } else {
    return await _NativeWebBrowser.default.getCustomTabsSupportingBrowsersAsync();
  }
}
/**
 * This method calls `warmUp` method on [CustomTabsClient](https://developer.android.com/reference/android/support/customtabs/CustomTabsClient.html#warmup(long))
 * for specified package.
 *
 * @param browserPackage Package of browser to be warmed up. If not set, preferred browser will be warmed.
 *
 * @return A promise which fulfils with `WebBrowserWarmUpResult` object.
 * @platform android
 */


async function warmUpAsync(browserPackage) {
  if (!_NativeWebBrowser.default.warmUpAsync) {
    throw new _WebBrowser.UnavailabilityError('WebBrowser', 'warmUpAsync');
  }

  if (_reactNative.Platform.OS !== 'android') {
    return {};
  } else {
    return await _NativeWebBrowser.default.warmUpAsync(browserPackage);
  }
}
/**
 * This method initiates (if needed) [CustomTabsSession](https://developer.android.com/reference/android/support/customtabs/CustomTabsSession.html#maylaunchurl)
 * and calls its `mayLaunchUrl` method for browser specified by the package.
 *
 * @param url The url of page that is likely to be loaded first when opening browser.
 * @param browserPackage Package of browser to be informed. If not set, preferred
 * browser will be used.
 *
 * @return A promise which fulfils with `WebBrowserMayInitWithUrlResult` object.
 * @platform android
 */


async function mayInitWithUrlAsync(url, browserPackage) {
  if (!_NativeWebBrowser.default.mayInitWithUrlAsync) {
    throw new _WebBrowser.UnavailabilityError('WebBrowser', 'mayInitWithUrlAsync');
  }

  if (_reactNative.Platform.OS !== 'android') {
    return {};
  } else {
    return await _NativeWebBrowser.default.mayInitWithUrlAsync(url, browserPackage);
  }
}
/**
 * This methods removes all bindings to services created by [`warmUpAsync`](#webbrowserwarmupasyncbrowserpackage)
 * or [`mayInitWithUrlAsync`](#webbrowsermayinitwithurlasyncurl-browserpackage). You should call
 * this method once you don't need them to avoid potential memory leaks. However, those binding
 * would be cleared once your application is destroyed, which might be sufficient in most cases.
 *
 * @param browserPackage Package of browser to be cooled. If not set, preferred browser will be used.
 *
 * @return The promise which fulfils with ` WebBrowserCoolDownResult` when cooling is performed, or
 * an empty object when there was no connection to be dismissed.
 * @platform android
 */


async function coolDownAsync(browserPackage) {
  if (!_NativeWebBrowser.default.coolDownAsync) {
    throw new _WebBrowser.UnavailabilityError('WebBrowser', 'coolDownAsync');
  }

  if (_reactNative.Platform.OS !== 'android') {
    return {};
  } else {
    return await _NativeWebBrowser.default.coolDownAsync(browserPackage);
  }
}

let browserLocked = false; // @needsAudit

/**
 * Opens the url with Safari in a modal on iOS using [`SFSafariViewController`](https://developer.apple.com/documentation/safariservices/sfsafariviewcontroller),
 * and Chrome in a new [custom tab](https://developer.chrome.com/multidevice/android/customtabs)
 * on Android. On iOS, the modal Safari will not share cookies with the system Safari. If you need
 * this, use [`openAuthSessionAsync`](#webbrowseropenauthsessionasyncurl-redirecturl-browserparams).
 *
 * @param url The url to open in the web browser.
 * @param browserParams A dictionary of key-value pairs.
 *
 * @return The promise behaves differently based on the platform.
 * On Android promise resolves with `{type: 'opened'}` if we were able to open browser.
 * On iOS:
 * - If the user closed the web browser, the Promise resolves with `{ type: 'cancel' }`.
 * - If the browser is closed using [`dismissBrowser`](#webbrowserdismissbrowser), the Promise resolves with `{ type: 'dismiss' }`.
 */

async function openBrowserAsync(url) {
  let browserParams = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};

  if (!_NativeWebBrowser.default.openBrowserAsync) {
    throw new _WebBrowser.UnavailabilityError('WebBrowser', 'openBrowserAsync');
  }

  if (browserLocked) {
    // Prevent multiple sessions from running at the same time, WebBrowser doesn't
    // support it this makes the behavior predictable.
    if (__DEV__) {
      console.warn('Attempted to call WebBrowser.openBrowserAsync multiple times while already active. Only one WebBrowser controller can be active at any given time.');
    }

    return {
      type: _WebBrowser.WebBrowserResultType.LOCKED
    };
  }

  browserLocked = true;
  let result;

  try {
    result = await _NativeWebBrowser.default.openBrowserAsync(url, browserParams);
  } finally {
    // WebBrowser session complete, unset lock
    browserLocked = false;
  }

  return result;
}
/**
 * Dismisses the presented web browser.
 *
 * @return The `void` on successful attempt, or throws error, if dismiss functionality is not avaiable.
 * @platform ios
 */


function dismissBrowser() {
  if (!_NativeWebBrowser.default.dismissBrowser) {
    throw new _WebBrowser.UnavailabilityError('WebBrowser', 'dismissBrowser');
  }

  _NativeWebBrowser.default.dismissBrowser();
}
/**
 * # On iOS:
 * Opens the url with Safari in a modal using `SFAuthenticationSession` on iOS 11 and greater,
 * and falling back on a `SFSafariViewController`. The user will be asked whether to allow the app
 * to authenticate using the given url.
 *
 * # On Android:
 * This will be done using a "custom Chrome tabs" browser, [AppState](../react-native/appstate/),
 * and [Linking](./linking/) APIs.
 *
 * # On web:
 * > This API can only be used in a secure environment (`https`). You can use expo `start:web --https`
 * to test this. Otherwise, an error with code [`ERR_WEB_BROWSER_CRYPTO`](#errwebbrowsercrypto) will be thrown.
 * This will use the browser's [`window.open()`](https://developer.mozilla.org/en-US/docs/Web/API/Window/open) API.
 * - _Desktop_: This will create a new web popup window in the browser that can be closed later using `WebBrowser.maybeCompleteAuthSession()`.
 * - _Mobile_: This will open a new tab in the browser which can be closed using `WebBrowser.maybeCompleteAuthSession()`.
 *
 * How this works on web:
 * - A crypto state will be created for verifying the redirect.
 *   - This means you need to run with `expo start:web --https`
 * - The state will be added to the window's `localstorage`. This ensures that auth cannot complete
 *   unless it's done from a page running with the same origin as it was started.
 *   Ex: if `openAuthSessionAsync` is invoked on `https://localhost:19006`, then `maybeCompleteAuthSession`
 *   must be invoked on a page hosted from the origin `https://localhost:19006`. Using a different
 *   website, or even a different host like `https://128.0.0.*:19006` for example will not work.
 * - A timer will be started to check for every 1000 milliseconds (1 second) to detect if the window
 *   has been closed by the user. If this happens then a promise will resolve with `{ type: 'dismiss' }`.
 *
 * > On mobile web, Chrome and Safari will block any call to [`window.open()`](https://developer.mozilla.org/en-US/docs/Web/API/Window/open)
 * which takes too long to fire after a user interaction. This method must be invoked immediately
 * after a user interaction. If the event is blocked, an error with code [`ERR_WEB_BROWSER_BLOCKED`](#errwebbrowserblocked) will be thrown.
 *
 * @param url The url to open in the web browser. This should be a login page.
 * @param redirectUrl _Optional_ - The url to deep link back into your app. By default, this will be [`Constants.linkingUrl`](./constants/#expoconstantslinkinguri).
 * @param options _Optional_ - An object extending the [`WebBrowserOpenOptions`](#webbrowseropenoptions).
 * If there is no native AuthSession implementation available (which is the case on Android)
 * these params will be used in the browser polyfill. If there is a native AuthSession implementation,
 * these params will be ignored.
 *
 * @return
 * - If the user does not permit the application to authenticate with the given url, the Promise fulfills with `{ type: 'cancel' }` object.
 * - If the user closed the web browser, the Promise fulfills with `{ type: 'cancel' }` object.
 * - If the browser is closed using [`dismissBrowser`](#webbrowserdismissbrowser),
 * the Promise fulfills with `{ type: 'dismiss' }` object.
 */


async function openAuthSessionAsync(url, redirectUrl) {
  let options = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};

  if (_authSessionIsNativelySupported()) {
    if (!_NativeWebBrowser.default.openAuthSessionAsync) {
      throw new _WebBrowser.UnavailabilityError('WebBrowser', 'openAuthSessionAsync');
    }

    if (['ios', 'web'].includes(_reactNative.Platform.OS)) {
      return _NativeWebBrowser.default.openAuthSessionAsync(url, redirectUrl, options);
    }

    return _NativeWebBrowser.default.openAuthSessionAsync(url, redirectUrl);
  } else {
    return _openAuthSessionPolyfillAsync(url, redirectUrl, options);
  }
} // @docsMissing


function dismissAuthSession() {
  if (_authSessionIsNativelySupported()) {
    if (!_NativeWebBrowser.default.dismissAuthSession) {
      throw new _WebBrowser.UnavailabilityError('WebBrowser', 'dismissAuthSession');
    }

    _NativeWebBrowser.default.dismissAuthSession();
  } else {
    if (!_NativeWebBrowser.default.dismissBrowser) {
      throw new _WebBrowser.UnavailabilityError('WebBrowser', 'dismissAuthSession');
    }

    _NativeWebBrowser.default.dismissBrowser();
  }
}
/**
 * Possibly completes an authentication session on web in a window popup. The method
 * should be invoked on the page that the window redirects to.
 *
 * @param options
 *
 * @return Returns an object with message about why the redirect failed or succeeded:
 *
 * If `type` is set to `failed`, the reason depends on the message:
 * - `Not supported on this platform`: If the platform doesn't support this method (iOS, Android).
 * - `Cannot use expo-web-browser in a non-browser environment`: If the code was executed in an SSR
 *   or node environment.
 * - `No auth session is currently in progress`: (the cached state wasn't found in local storage).
 *   This can happen if the window redirects to an origin (website) that is different to the initial
 *   website origin. If this happens in development, it may be because the auth started on localhost
 *   and finished on your computer port (Ex: `128.0.0.*`). This is controlled by the `redirectUrl`
 *   and `returnUrl`.
 * - `Current URL "<URL>" and original redirect URL "<URL>" do not match`: This can occur when the
 *   redirect URL doesn't match what was initial defined as the `returnUrl`. You can skip this test
 *   in development by passing `{ skipRedirectCheck: true }` to the function.
 *
 * If `type` is set to `success`, the parent window will attempt to close the child window immediately.
 *
 * If the error `ERR_WEB_BROWSER_REDIRECT` was thrown, it may mean that the parent window was
 * reloaded before the auth was completed. In this case you'll need to close the child window manually.
 *
 * @platform web
 */


function maybeCompleteAuthSession() {
  let options = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {};

  if (_NativeWebBrowser.default.maybeCompleteAuthSession) {
    return _NativeWebBrowser.default.maybeCompleteAuthSession(options);
  }

  return {
    type: 'failed',
    message: 'Not supported on this platform'
  };
}
/* iOS <= 10 and Android polyfill for SFAuthenticationSession flow */


function _authSessionIsNativelySupported() {
  if (_reactNative.Platform.OS === 'android') {
    return false;
  } else if (_reactNative.Platform.OS === 'web') {
    return true;
  }

  const versionNumber = parseInt(String(_reactNative.Platform.Version), 10);
  return versionNumber >= 11;
}

let _redirectHandler = null;
/*
 * openBrowserAsync on Android doesn't wait until closed, so we need to polyfill
 * it with AppState
 */
// Store the `resolve` function from a Promise to fire when the AppState
// returns to active

let _onWebBrowserCloseAndroid = null; // If the initial AppState.currentState is null, we assume that the first call to
// AppState#change event is not actually triggered by a real change,
// is triggered instead by the bridge capturing the current state
// (https://reactnative.dev/docs/appstate#basic-usage)

let _isAppStateAvailable = _reactNative.AppState.currentState !== null;

function _onAppStateChangeAndroid(state) {
  if (!_isAppStateAvailable) {
    _isAppStateAvailable = true;
    return;
  }

  if (state === 'active' && _onWebBrowserCloseAndroid) {
    _onWebBrowserCloseAndroid();
  }
}

async function _openBrowserAndWaitAndroidAsync(startUrl) {
  let browserParams = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};
  const appStateChangedToActive = new Promise(resolve => {
    _onWebBrowserCloseAndroid = resolve;
  });

  const stateChangeSubscription = _reactNative.AppState.addEventListener('change', _onAppStateChangeAndroid);

  let result = {
    type: _WebBrowser.WebBrowserResultType.CANCEL
  };
  let type = null;

  try {
    ({
      type
    } = await openBrowserAsync(startUrl, browserParams));
  } catch (e) {
    stateChangeSubscription === null || stateChangeSubscription === void 0 ? void 0 : stateChangeSubscription.remove();
    _onWebBrowserCloseAndroid = null;
    throw e;
  }

  if (type === 'opened') {
    await appStateChangedToActive;
    result = {
      type: _WebBrowser.WebBrowserResultType.DISMISS
    };
  }

  stateChangeSubscription === null || stateChangeSubscription === void 0 ? void 0 : stateChangeSubscription.remove();
  _onWebBrowserCloseAndroid = null;
  return result;
}

async function _openAuthSessionPolyfillAsync(startUrl, returnUrl) {
  let browserParams = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};

  if (_redirectHandler) {
    throw new Error(`The WebBrowser's auth session is in an invalid state with a redirect handler set when it should not be`);
  }

  if (_onWebBrowserCloseAndroid) {
    throw new Error(`WebBrowser is already open, only one can be open at a time`);
  }

  try {
    if (_reactNative.Platform.OS === 'android') {
      return await Promise.race([_openBrowserAndWaitAndroidAsync(startUrl, browserParams), _waitForRedirectAsync(returnUrl)]);
    } else {
      return await Promise.race([openBrowserAsync(startUrl, browserParams), _waitForRedirectAsync(returnUrl)]);
    }
  } finally {
    // We can't dismiss the browser on Android, only call this when it's available.
    // Users on Android need to manually press the 'x' button in Chrome Custom Tabs, sadly.
    if (_NativeWebBrowser.default.dismissBrowser) {
      _NativeWebBrowser.default.dismissBrowser();
    }

    _stopWaitingForRedirect();
  }
}

function _stopWaitingForRedirect() {
  if (!_redirectHandler) {
    throw new Error(`The WebBrowser auth session is in an invalid state with no redirect handler when one should be set`);
  }

  _reactNative.Linking.removeEventListener('url', _redirectHandler);

  _redirectHandler = null;
}

function _waitForRedirectAsync(returnUrl) {
  return new Promise(resolve => {
    _redirectHandler = event => {
      if (event.url.startsWith(returnUrl)) {
        resolve({
          url: event.url,
          type: 'success'
        });
      }
    };

    _reactNative.Linking.addEventListener('url', _redirectHandler);
  });
}
//# sourceMappingURL=index.js.map