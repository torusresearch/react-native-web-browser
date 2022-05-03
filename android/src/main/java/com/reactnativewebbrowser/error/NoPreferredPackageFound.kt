package com.reactnativewebbrowser.error

class NoPreferredPackageFound(str: String) :
  ReactNativeWebBrowserException(str) {
  constructor() : this("PREFERRED_PACKAGE_NOT_FOUND") {}
}
