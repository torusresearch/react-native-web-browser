import compareUrls from 'compare-urls';
import { AppState, Dimensions, Platform } from 'react-native';
import { WebBrowserResultType } from './WebBrowser.types';
const isDOMAvailable = Platform.OS === 'web';
const POPUP_WIDTH = 500;
const POPUP_HEIGHT = 650;
let popupWindow = null;
const listenerMap = new Map();

const getHandle = () => 'ExpoWebBrowserRedirectHandle';

const getOriginUrlHandle = hash => `ExpoWebBrowser_OriginUrl_${hash}`;

const getRedirectUrlHandle = hash => `ExpoWebBrowser_RedirectUrl_${hash}`;

function dismissPopup() {
  if (!popupWindow) {
    return;
  }

  popupWindow.close();

  if (listenerMap.has(popupWindow)) {
    const {
      listener,
      appStateListener,
      interval
    } = listenerMap.get(popupWindow);
    clearInterval(interval);
    window.removeEventListener('message', listener);
    AppState.removeEventListener('change', appStateListener);
    listenerMap.delete(popupWindow);
    const handle = window.localStorage.getItem(getHandle());

    if (handle) {
      window.localStorage.removeItem(getHandle());
      window.localStorage.removeItem(getOriginUrlHandle(handle));
      window.localStorage.removeItem(getRedirectUrlHandle(handle));
    }

    popupWindow = null;
  }
}

export default {
  get name() {
    return 'ExpoWebBrowser';
  },

  async openBrowserAsync(url) {
    let browserParams = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};
    if (!isDOMAvailable) return {
      type: WebBrowserResultType.CANCEL
    };
    const {
      windowName = '_blank',
      windowFeatures
    } = browserParams;
    const features = getPopupFeaturesString(windowFeatures);
    window.open(url, windowName, features);
    return {
      type: WebBrowserResultType.OPENED
    };
  },

  dismissAuthSession() {
    if (!isDOMAvailable) return;
    dismissPopup();
  },

  maybeCompleteAuthSession(_ref) {
    var _window$opener;

    let {
      skipRedirectCheck
    } = _ref;

    if (!isDOMAvailable) {
      return {
        type: 'failed',
        message: 'Cannot use expo-web-browser in a non-browser environment'
      };
    }

    const handle = window.localStorage.getItem(getHandle());

    if (!handle) {
      return {
        type: 'failed',
        message: 'No auth session is currently in progress'
      };
    }

    const url = window.location.href;

    if (skipRedirectCheck !== true) {
      const redirectUrl = window.localStorage.getItem(getRedirectUrlHandle(handle)); // Compare the original redirect url against the current url with it's query params removed.

      const currentUrl = window.location.origin + window.location.pathname;

      if (!compareUrls(redirectUrl, currentUrl)) {
        return {
          type: 'failed',
          message: `Current URL "${currentUrl}" and original redirect URL "${redirectUrl}" do not match.`
        };
      }
    } // Save the link for app state listener


    window.localStorage.setItem(getOriginUrlHandle(handle), url); // Get the window that created the current popup

    const parent = (_window$opener = window.opener) !== null && _window$opener !== void 0 ? _window$opener : window.parent;

    if (!parent) {
      throw new Error("ERR_WEB_BROWSER_REDIRECT: The window cannot complete the redirect request because the invoking window doesn't have a reference to it's parent. This can happen if the parent window was reloaded.");
    } // Send the URL back to the opening window.


    parent.postMessage({
      url,
      expoSender: handle
    }, parent.location.toString());
    return {
      type: 'success',
      message: `Attempting to complete auth`
    }; // Maybe set timer to throw an error if the window is still open after attempting to complete.
  },

  // This method should be invoked from user input.
  async openAuthSessionAsync(url, redirectUrl, openOptions) {
    var _redirectUrl, _popupWindow;

    if (!isDOMAvailable) return {
      type: WebBrowserResultType.CANCEL
    };
    redirectUrl = (_redirectUrl = redirectUrl) !== null && _redirectUrl !== void 0 ? _redirectUrl : getRedirectUrlFromUrlOrGenerate(url);
    const state = await getStateFromUrlOrGenerateAsync(url); // Save handle for session

    window.localStorage.setItem(getHandle(), state); // Save redirect Url for further verification

    window.localStorage.setItem(getRedirectUrlHandle(state), redirectUrl);

    if (popupWindow == null || (_popupWindow = popupWindow) !== null && _popupWindow !== void 0 && _popupWindow.closed) {
      const features = getPopupFeaturesString(openOptions === null || openOptions === void 0 ? void 0 : openOptions.windowFeatures);
      popupWindow = window.open(url, openOptions === null || openOptions === void 0 ? void 0 : openOptions.windowName, features);

      if (popupWindow) {
        try {
          popupWindow.focus();
        } catch {}
      } else {
        throw new Error('ERR_WEB_BROWSER_BLOCKED: Popup window was blocked by the browser or failed to open. This can happen in mobile browsers when the window.open() method was invoked too long after a user input was fired.');
      }
    }

    return new Promise(async resolve => {
      // Create a listener for messages sent from the popup
      const listener = event => {
        if (!event.isTrusted) return; // Ensure we trust the sender.

        if (event.origin !== window.location.origin) {
          return;
        }

        const {
          data
        } = event; // Use a crypto hash to invalid message.

        const handle = window.localStorage.getItem(getHandle()); // Ensure the sender is also from expo-web-browser

        if (data.expoSender === handle) {
          dismissPopup();
          resolve({
            type: 'success',
            url: data.url
          });
        }
      }; // Add a listener for receiving messages from the popup.


      window.addEventListener('message', listener, false); // Create an app state listener as a fallback to the popup listener

      const appStateListener = state => {
        if (state !== 'active') {
          return;
        }

        const handle = window.localStorage.getItem(getHandle());

        if (handle) {
          const url = window.localStorage.getItem(getOriginUrlHandle(handle));

          if (url) {
            dismissPopup();
            resolve({
              type: 'success',
              url
            });
          }
        }
      };

      AppState.addEventListener('change', appStateListener); // Check if the window has been closed every second.

      const interval = setInterval(() => {
        var _popupWindow2;

        if ((_popupWindow2 = popupWindow) !== null && _popupWindow2 !== void 0 && _popupWindow2.closed) {
          if (resolve) resolve({
            type: WebBrowserResultType.DISMISS
          });
          clearInterval(interval);
          dismissPopup();
        }
      }, 1000); // Store the listener and interval for clean up.

      listenerMap.set(popupWindow, {
        listener,
        interval,
        appStateListener
      });
    });
  }

}; // Crypto

function isCryptoAvailable() {
  var _window;

  if (!isDOMAvailable) return false;
  return !!((_window = window) !== null && _window !== void 0 && _window.crypto);
}

function isSubtleCryptoAvailable() {
  if (!isCryptoAvailable()) return false;
  return !!window.crypto.subtle;
}

async function getStateFromUrlOrGenerateAsync(inputUrl) {
  const url = new URL(inputUrl);

  if (url.searchParams.has('state') && typeof url.searchParams.get('state') === 'string') {
    // Ensure we reuse the auth state if it's passed in.
    return url.searchParams.get('state');
  } // Generate a crypto state for verifying the return popup.


  return await generateStateAsync();
}

function getRedirectUrlFromUrlOrGenerate(inputUrl) {
  const url = new URL(inputUrl);

  if (url.searchParams.has('redirect_uri') && typeof url.searchParams.get('redirect_uri') === 'string') {
    // Ensure we reuse the redirect_uri if it's passed in the input url.
    return url.searchParams.get('redirect_uri');
  } // Emulate how native uses Constants.linkingUrl


  return location.origin + location.pathname;
}

const CHARSET = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';

async function generateStateAsync() {
  if (!isSubtleCryptoAvailable()) {
    throw new Error("ERR_WEB_BROWSER_CRYPTO: The current environment doesn't support crypto. Ensure you are running from a secure origin (https).");
  }

  const encoder = new TextEncoder();
  const data = generateRandom(10);
  const buffer = encoder.encode(data);
  const hashedData = await crypto.subtle.digest('SHA-256', buffer);
  const state = btoa(String.fromCharCode(...new Uint8Array(hashedData)));
  return state;
}

function generateRandom(size) {
  let arr = new Uint8Array(size);

  if (arr.byteLength !== arr.length) {
    arr = new Uint8Array(arr.buffer);
  }

  const array = new Uint8Array(arr.length);

  if (isCryptoAvailable()) {
    window.crypto.getRandomValues(array);
  } else {
    for (let i = 0; i < size; i += 1) {
      array[i] = Math.random() * CHARSET.length | 0;
    }
  }

  return bufferToString(array);
}

function bufferToString(buffer) {
  const state = [];

  for (let i = 0; i < buffer.byteLength; i += 1) {
    const index = buffer[i] % CHARSET.length;
    state.push(CHARSET[index]);
  }

  return state.join('');
} // Window Features
// Ensure feature string is an object


function normalizePopupFeaturesString(options) {
  let windowFeatures = {}; // This should be avoided because it adds extra time to the popup command.

  if (typeof options === 'string') {
    // Convert string of `key=value,foo=bar` into an object
    const windowFeaturePairs = options.split(',');

    for (const pair of windowFeaturePairs) {
      const [key, value] = pair.trim().split('=');

      if (key && value) {
        windowFeaturePairs[key] = value;
      }
    }
  } else if (options) {
    windowFeatures = options;
  }

  return windowFeatures;
} // Apply default values to the input feature set


function getPopupFeaturesString(options) {
  var _windowFeatures$width, _windowFeatures$heigh, _windowFeatures$top, _windowFeatures$left, _windowFeatures$toolb, _windowFeatures$menub, _windowFeatures$locat, _windowFeatures$resiz, _windowFeatures$statu, _windowFeatures$scrol;

  const windowFeatures = normalizePopupFeaturesString(options);
  const width = (_windowFeatures$width = windowFeatures.width) !== null && _windowFeatures$width !== void 0 ? _windowFeatures$width : POPUP_WIDTH;
  const height = (_windowFeatures$heigh = windowFeatures.height) !== null && _windowFeatures$heigh !== void 0 ? _windowFeatures$heigh : POPUP_HEIGHT;
  const dimensions = Dimensions.get('screen');
  const top = (_windowFeatures$top = windowFeatures.top) !== null && _windowFeatures$top !== void 0 ? _windowFeatures$top : Math.max(0, (dimensions.height - height) * 0.5);
  const left = (_windowFeatures$left = windowFeatures.left) !== null && _windowFeatures$left !== void 0 ? _windowFeatures$left : Math.max(0, (dimensions.width - width) * 0.5); // Create a reasonable popup
  // https://developer.mozilla.org/en-US/docs/Web/API/Window/open#Window_features

  return featureObjectToString({ ...windowFeatures,
    // Toolbar buttons (Back, Forward, Reload, Stop buttons).
    toolbar: (_windowFeatures$toolb = windowFeatures.toolbar) !== null && _windowFeatures$toolb !== void 0 ? _windowFeatures$toolb : 'no',
    menubar: (_windowFeatures$menub = windowFeatures.menubar) !== null && _windowFeatures$menub !== void 0 ? _windowFeatures$menub : 'no',
    // Shows the location bar or the address bar.
    location: (_windowFeatures$locat = windowFeatures.location) !== null && _windowFeatures$locat !== void 0 ? _windowFeatures$locat : 'yes',
    resizable: (_windowFeatures$resiz = windowFeatures.resizable) !== null && _windowFeatures$resiz !== void 0 ? _windowFeatures$resiz : 'yes',
    // If this feature is on, then the new secondary window has a status bar.
    status: (_windowFeatures$statu = windowFeatures.status) !== null && _windowFeatures$statu !== void 0 ? _windowFeatures$statu : 'no',
    scrollbars: (_windowFeatures$scrol = windowFeatures.scrollbars) !== null && _windowFeatures$scrol !== void 0 ? _windowFeatures$scrol : 'yes',
    top,
    left,
    width,
    height
  });
}

export function featureObjectToString(features) {
  return Object.keys(features).reduce((prev, current) => {
    let value = features[current];

    if (typeof value === 'boolean') {
      value = value ? 'yes' : 'no';
    }

    if (current && value) {
      if (prev) prev += ',';
      return `${prev}${current}=${value}`;
    }

    return prev;
  }, '');
}
//# sourceMappingURL=NativeWebBrowser.web.js.map