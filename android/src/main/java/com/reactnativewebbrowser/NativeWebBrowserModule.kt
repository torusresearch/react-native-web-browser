package com.reactnativewebbrowser

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import androidx.browser.customtabs.CustomTabsIntent
import com.facebook.react.bridge.*
import com.reactnativewebbrowser.error.CurrentActivityNotFoundException
import com.reactnativewebbrowser.error.NoPreferredPackageFound
import com.reactnativewebbrowser.error.PackageManagerNotFoundException

class NativeWebBrowserModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  val activityProvider = InternalActicityProvider(reactContext)
  val customTabsActivitiesHelper =
    InternalCustomTabsActivitiesHelper(activityProvider)
  val customTabsConnectionHelper =
    InternalCustomTabsConnectionHelper(reactContext)

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

  @ExpoMethod
  fun warmUpAsync(packageName: String?, promise: Promise) {
    try {
      val packageName = givenOrPreferredPackageName(packageName)
      customTabsConnectionHelper.warmUp(packageName)
      val result = Bundle()
      result.putString(
        expo.modules.webbrowser.WebBrowserModule.SERVICE_PACKAGE_KEY,
        packageName
      )
      promise.resolve(result)
    } catch (ex: NoPreferredPackageFound) {
      promise.reject(ex)
    }
  }

  @ExpoMethod
  fun coolDownAsync(packageName: String?, promise: Promise) {
    var packageName = packageName
    try {
      packageName = givenOrPreferredPackageName(packageName)
      if (mConnectionHelper.coolDown(packageName)) {
        val result = Bundle()
        result.putString(
          expo.modules.webbrowser.WebBrowserModule.SERVICE_PACKAGE_KEY,
          packageName
        )
        promise.resolve(result)
      } else {
        promise.resolve(Bundle())
      }
    } catch (ex: NoPreferredPackageFound) {
      promise.reject(ex)
    }
  }

  @ExpoMethod
  fun mayInitWithUrlAsync(
    url: String?,
    packageName: String?,
    promise: Promise
  ) {
    var packageName = packageName
    try {
      packageName = givenOrPreferredPackageName(packageName)
      mConnectionHelper.mayInitWithUrl(packageName, Uri.parse(url))
      val result = Bundle()
      result.putString(
        expo.modules.webbrowser.WebBrowserModule.SERVICE_PACKAGE_KEY,
        packageName
      )
      promise.resolve(result)
    } catch (ex: NoPreferredPackageFound) {
      promise.reject(ex)
    }
  }

  @ExpoMethod
  fun getCustomTabsSupportingBrowsersAsync(promise: Promise) {
    try {
      val activities: ArrayList<String> =
        mCustomTabsResolver.getCustomTabsResolvingActivities()
      val services: ArrayList<String> =
        mCustomTabsResolver.getCustomTabsResolvingServices()
      val preferredPackage: String =
        mCustomTabsResolver.getPreferredCustomTabsResolvingActivity(activities)
      val defaultPackage: String =
        mCustomTabsResolver.getDefaultCustomTabsResolvingActivity()
      var defaultCustomTabsPackage: String? = null
      if (activities.contains(defaultPackage)) { // It might happen, that default activity does not support Chrome Tabs. Then it will be ResolvingActivity and we don't want to return it as a result.
        defaultCustomTabsPackage = defaultPackage
      }
      val result = Bundle()
      result.putStringArrayList(
        expo.modules.webbrowser.WebBrowserModule.BROWSER_PACKAGES_KEY,
        activities
      )
      result.putStringArrayList(
        expo.modules.webbrowser.WebBrowserModule.SERVICE_PACKAGES_KEY,
        services
      )
      result.putString(
        expo.modules.webbrowser.WebBrowserModule.PREFERRED_BROWSER_PACKAGE,
        preferredPackage
      )
      result.putString(
        expo.modules.webbrowser.WebBrowserModule.DEFAULT_BROWSER_PACKAGE,
        defaultCustomTabsPackage
      )
      promise.resolve(result)
    } catch (ex: CurrentActivityNotFoundException) {
      promise.reject(ex)
    } catch (ex: PackageManagerNotFoundException) {
      promise.reject(ex)
    }
  }

  /**
   * @param url Url to be opened by WebBrowser
   * @param arguments Required arguments are:
   * toolbarColor: String;
   * browserPackage: String;
   * enableBarCollapsing: Boolean;
   * showTitle: Boolean;
   * enableDefaultShareMenuItem: Boolean;
   * showInRecents: Boolean;
   * @param promise
   */
  @ExpoMethod
  fun openBrowserAsync(
    url: String?,
    arguments: ReadableArguments,
    promise: Promise
  ) {
    val intent = createCustomTabsIntent(arguments)
    intent.data = Uri.parse(url)
    try {
      if (mCustomTabsResolver.canResolveIntent(intent)) {
        mCustomTabsResolver.startCustomTabs(intent)
        val result = Bundle()
        result.putString("type", "opened")
        promise.resolve(result)
      } else {
        promise.reject(
          expo.modules.webbrowser.WebBrowserModule.ERROR_CODE,
          "No matching activity!"
        )
      }
    } catch (ex: CurrentActivityNotFoundException) {
      promise.reject(ex)
    } catch (ex: PackageManagerNotFoundException) {
      promise.reject(ex)
    }
  }

  private fun createCustomTabsIntent(arguments: ReadableMap): Intent {
    val builder = CustomTabsIntent.Builder()
    val color: String? =
      arguments.getString(TOOLBAR_COLOR_KEY)
    val secondaryColor: String? =
      arguments.getString(SECONDARY_TOOLBAR_COLOR_KEY)
    val packageName: String? =
      arguments.getString(BROWSER_PACKAGE_KEY)
    try {
      if (!TextUtils.isEmpty(color)) {
        val intColor = Color.parseColor(color)
        builder.setToolbarColor(intColor)
      }
      if (!TextUtils.isEmpty(secondaryColor)) {
        val intSecondaryColor = Color.parseColor(secondaryColor)
        builder.setSecondaryToolbarColor(intSecondaryColor)
      }
    } catch (ignored: IllegalArgumentException) {
    }
    builder.setShowTitle(
      if (!arguments.hasKey(SHOW_TITLE_KEY) || arguments.isNull(SHOW_TITLE_KEY)) {
        false
      } else {
        arguments.getBoolean(
          SHOW_TITLE_KEY
        )
      }
    )
    if (arguments.hasKey(DEFAULT_SHARE_MENU_ITEM) && arguments.getBoolean(
        DEFAULT_SHARE_MENU_ITEM
      )
    ) {
      builder.addDefaultShareMenuItem()
    }
    val intent = builder.build().intent

    // We cannot use builder's method enableUrlBarHiding, because there is no corresponding disable method and some browsers enables it by default.
    intent.putExtra(
      CustomTabsIntent.EXTRA_ENABLE_URLBAR_HIDING,
      arguments.getBoolean(
        ENABLE_BAR_COLLAPSING_KEY,
        false
      )
    )
    if (!TextUtils.isEmpty(packageName)) {
      intent.setPackage(packageName)
    }
    if (arguments.getBoolean(
        CREATE_TASK,
        true
      )
    ) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      if (!arguments.getBoolean(
          SHOW_IN_RECENTS,
          false
        )
      ) {
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
      }
    }
    return intent
  }

  private fun givenOrPreferredPackageName(packageName: String?): String? {
    var processedPackageName = packageName
    try {
      if (TextUtils.isEmpty(processedPackageName)) {
        processedPackageName =
          customTabsActivitiesHelper.getPreferredCustomTabsResolvingActivity(
            null
          )
      }
    } catch (ex: CurrentActivityNotFoundException) {
      throw NoPreferredPackageFound(NO_PREFERRED_PACKAGE_MSG)
    } catch (ex: PackageManagerNotFoundException) {
      throw NoPreferredPackageFound(NO_PREFERRED_PACKAGE_MSG)
    }
    if (TextUtils.isEmpty(processedPackageName)) {
      throw NoPreferredPackageFound(NO_PREFERRED_PACKAGE_MSG)
    }
    return processedPackageName
  }


}
