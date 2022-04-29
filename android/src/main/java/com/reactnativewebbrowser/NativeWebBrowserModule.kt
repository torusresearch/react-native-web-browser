package com.reactnativewebbrowser

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class NativeWebBrowserModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  val activityProvider = InternalActicityProvider(reactContext)
  val customTabsActivitiesHelper = InternalCustomTabsActivitiesHelper(activityProvider)
  val customTabsConnectionHelper = InternalCustomTabsConnectionHelper(reactContext)

  private val BROWSER_PACKAGE_KEY = "browserPackage"
  private val SERVICE_PACKAGE_KEY = "servicePackage"
  private val BROWSER_PACKAGES_KEY = "browserPackages"
  private val SERVICE_PACKAGES_KEY = "servicePackages"
  private val PREFERRED_BROWSER_PACKAGE = "preferredBrowserPackage"
  private val DEFAULT_BROWSER_PACKAGE = "defaultBrowserPackage"
  private val SHOW_IN_RECENTS = "showInRecents"
  private val CREATE_TASK = "createTask"
  private val DEFAULT_SHARE_MENU_ITEM = "enableDefaultShareMenuItem"
  private val TOOLBAR_COLOR_KEY = "toolbarColor"
  private val SECONDARY_TOOLBAR_COLOR_KEY = "secondaryToolbarColor"

  private val ERROR_CODE = "RNWebBrowser"
  private val TAG = "ReactNativeWebBrowser"
  private val SHOW_TITLE_KEY = "showTitle"
  private val ENABLE_BAR_COLLAPSING_KEY = "enableBarCollapsing"

  private val NO_PREFERRED_PACKAGE_MSG =
    "Cannot determine preferred package without satisfying it."

  override fun getName(): String {
    return "NativeWebBrowser"
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  fun multiply(a: Int, b: Int, promise: Promise) {

    promise.resolve(a * b)

  }


}
