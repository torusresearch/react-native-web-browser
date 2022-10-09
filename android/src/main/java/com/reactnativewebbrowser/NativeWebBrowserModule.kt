package com.reactnativewebbrowser

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.TextUtils
import androidx.browser.customtabs.CustomTabsIntent
import com.facebook.react.bridge.*
import com.reactnativewebbrowser.error.CurrentActivityNotFoundException
import com.reactnativewebbrowser.error.NoPreferredPackageFound
import com.reactnativewebbrowser.error.PackageManagerNotFoundException

class NativeWebBrowserModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  val activityProvider = InternalActivityProvider(reactContext)
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

  @ReactMethod
  fun warmUpAsync(packageName: String?, promise: Promise) {
    try {
      val processedPackageName = givenOrPreferredPackageName(packageName)
      customTabsConnectionHelper.warmUp(processedPackageName)
      val result = Arguments.createMap()
      result.putString(
        SERVICE_PACKAGE_KEY,
        processedPackageName
      )
      promise.resolve(result)
    } catch (ex: NoPreferredPackageFound) {
      promise.reject(ex)
    }
  }

  @ReactMethod
  fun coolDownAsync(packageName: String?, promise: Promise) {
    var processedPackageName = packageName
    try {
      processedPackageName = givenOrPreferredPackageName(processedPackageName)
      if (customTabsConnectionHelper.coolDown(processedPackageName)) {
        val result = Arguments.createMap()
        result.putString(
          SERVICE_PACKAGE_KEY,
          processedPackageName
        )
        promise.resolve(result)
      } else {
        promise.resolve(Arguments.createMap())
      }
    } catch (ex: NoPreferredPackageFound) {
      promise.reject(ex)
    }
  }

  @ReactMethod
  fun mayInitWithUrlAsync(
    url: String?,
    packageName: String?,
    promise: Promise
  ) {
    var processedPackageName = packageName
    try {
      processedPackageName = givenOrPreferredPackageName(processedPackageName)
      customTabsConnectionHelper.mayInitWithUrl(
        processedPackageName,
        Uri.parse(url)
      )
      val result = Arguments.createMap()
      result.putString(
        SERVICE_PACKAGE_KEY,
        processedPackageName
      )
      promise.resolve(result)
    } catch (ex: NoPreferredPackageFound) {
      promise.reject(ex)
    }
  }

  @ReactMethod
  fun getCustomTabsSupportingBrowsersAsync(promise: Promise) {
    try {
      val activities: ArrayList<String> =
        customTabsActivitiesHelper.customTabsResolvingActivities
      val services: ArrayList<String> =
        customTabsActivitiesHelper.customTabsResolvingServices
      val preferredPackage: String? =
        customTabsActivitiesHelper.getPreferredCustomTabsResolvingActivity(
          activities
        )
      val defaultPackage: String? =
        customTabsActivitiesHelper.defaultCustomTabsResolvingActivity
      var defaultCustomTabsPackage: String? = null
      if (activities.contains(defaultPackage)) { // It might happen, that default activity does not support Chrome Tabs. Then it will be ResolvingActivity and we don't want to return it as a result.
        defaultCustomTabsPackage = defaultPackage
      }
      val result = Arguments.createMap()
      result.putStringArrayList(
        BROWSER_PACKAGES_KEY,
        activities
      )
      result.putStringArrayList(
        SERVICE_PACKAGES_KEY,
        services
      )
      result.putString(
        PREFERRED_BROWSER_PACKAGE,
        preferredPackage
      )
      result.putString(
        DEFAULT_BROWSER_PACKAGE,
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
  @ReactMethod
  fun openBrowserAsync(
    url: String?,
    arguments: ReadableMap,
    promise: Promise
  ) {
    val intent = createCustomTabsIntent(arguments)
    intent.data = Uri.parse(url)
    try {
      if (customTabsActivitiesHelper.canResolveIntent(intent)) {
        customTabsActivitiesHelper.startCustomTabs(intent, Uri.parse(url))
        val result = Arguments.createMap()
        result.putString("type", "opened")
        promise.resolve(result)
      } else {
        promise.reject(
          ERROR_CODE,
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
      arguments.getBooleanWithDefault(
        SHOW_TITLE_KEY,
        false
      )
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
      arguments.getBooleanWithDefault(
        ENABLE_BAR_COLLAPSING_KEY,
        false
      )
    )
    if (!TextUtils.isEmpty(packageName)) {
      intent.setPackage(packageName)
    }
    if (arguments.getBooleanWithDefault(
        CREATE_TASK,
        true
      )
    ) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      if (!arguments.getBooleanWithDefault(
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

  private fun givenOrPreferredPackageName(packageName: String?): String {
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
    if (TextUtils.isEmpty(processedPackageName) || processedPackageName == null) {
      throw NoPreferredPackageFound(NO_PREFERRED_PACKAGE_MSG)
    }
    return processedPackageName
  }


}

fun ReadableMap.getBooleanWithDefault(
  name: String,
  defaultValue: Boolean
): Boolean {
  if (!this.hasKey(name) || this.isNull(name)) {
    return defaultValue
  }
  return this.getBoolean(name)
}

fun WritableMap.putStringArrayList(name: String, strArr: ArrayList<String>) {
  val arr = Arguments.createArray()
  for (str in strArr) {
    arr.pushString(str)
  }
  this.putArray(name, arr)
}
