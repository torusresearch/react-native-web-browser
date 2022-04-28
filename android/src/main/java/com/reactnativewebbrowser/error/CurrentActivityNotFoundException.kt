package com.reactnativewebbrowser.error

class CurrentActivityNotFoundException :
  ReactNativeWebBrowserException("Current activity not found. Make sure to call this method while your application is in foreground.") {
}
