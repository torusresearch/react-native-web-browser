package com.reactnativewebbrowser

import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import androidx.browser.customtabs.CustomTabsIntent
import com.facebook.react.bridge.*
import com.reactnativewebbrowser.error.CurrentActivityNotFoundException
import com.reactnativewebbrowser.error.NoPreferredPackageFound
import com.reactnativewebbrowser.error.PackageManagerNotFoundException
import com.reactnativewebbrowser.modules.OpenBrowserOptions
import com.reactnativewebbrowser.utilities.ifNull
import com.reactnativewebbrowser.utilities.putStringArrayList

private const val SERVICE_PACKAGE_KEY = "servicePackage"
private const val BROWSER_PACKAGES_KEY = "browserPackages"
private const val SERVICE_PACKAGES_KEY = "servicePackages"
private const val PREFERRED_BROWSER_PACKAGE = "preferredBrowserPackage"
private const val DEFAULT_BROWSER_PACKAGE = "defaultBrowserPackage"

private const val MODULE_NAME = "Web3AuthWebBrowser"
private const val ERROR_CODE = "RNWebBrowser"

class NativeWebBrowserModule(private val reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  private val activityProvider = InternalActivityProvider(reactContext)

  override fun initialize() {
    super.initialize()
    customTabsResolver = InternalCustomTabsActivitiesHelper(activityProvider)
    connectionHelper = InternalCustomTabsConnectionHelper(
      requireNotNull(reactContext) {
        "Cannot initialize WebBrowser, ReactContext is null"
      }
    )
  }

  override fun onCatalystInstanceDestroy() {
    super.onCatalystInstanceDestroy()
    connectionHelper.destroy()
  }

  @ReactMethod
  fun coolDownAsync(packageName: String?, promise: Promise) {
    var resolvedPackageName = packageName
    try {
      resolvedPackageName = givenOrPreferredPackageName(resolvedPackageName)
      if (connectionHelper.coolDown(resolvedPackageName)) {
        val result = Arguments.createMap()
        result.putString(
          SERVICE_PACKAGE_KEY,
          resolvedPackageName
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
  fun warmUpAsync(packageName: String?, promise: Promise) {
    try {
      val resolvedPackageName = givenOrPreferredPackageName(packageName)
      connectionHelper.warmUp(resolvedPackageName)
      val result = Arguments.createMap()
      result.putString(
        SERVICE_PACKAGE_KEY,
        resolvedPackageName
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
        customTabsResolver.customTabsResolvingActivities
      val services: ArrayList<String> =
        customTabsResolver.customTabsResolvingServices
      val preferredPackage: String? =
        customTabsResolver.getPreferredCustomTabsResolvingActivity(
          activities
        )
      val defaultPackage: String? =
        customTabsResolver.defaultCustomTabsResolvingActivity
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

  @ReactMethod
  fun mayInitWithUrlAsync(
    url: String?,
    packageName: String?,
    promise: Promise
  ) {
    var resolvedPackageName = packageName
    try {
      resolvedPackageName = givenOrPreferredPackageName(resolvedPackageName)
      connectionHelper.mayInitWithUrl(
        resolvedPackageName,
        Uri.parse(url)
      )
      val result = Arguments.createMap()
      result.putString(
        SERVICE_PACKAGE_KEY,
        resolvedPackageName
      )
      promise.resolve(result)
    } catch (ex: NoPreferredPackageFound) {
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
    arguments: OpenBrowserOptions,
    promise: Promise
  ) {
    val intent = createCustomTabsIntent(arguments)
    intent.data = Uri.parse(url)
    try {
      if (customTabsResolver.canResolveIntent(intent)) {
        customTabsResolver.startCustomTabs(intent)
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

  // these must be `internal` to be able to be injected in tests
  internal lateinit var customTabsResolver: InternalCustomTabsActivitiesHelper
  internal lateinit var connectionHelper: InternalCustomTabsConnectionHelper

  private fun createCustomTabsIntent(options: OpenBrowserOptions): Intent {
    val builder = CustomTabsIntent.Builder()

    val color = options.toolbarColor
    if (color != null) {
      builder.setToolbarColor(color)
    }

    val secondaryColor = options.secondaryToolbarColor
    if (secondaryColor != null) {
      builder.setSecondaryToolbarColor(secondaryColor)
    }

    builder.setShowTitle(options.showTitle)

    if (options.enableDefaultShareMenuItem) {
      builder.addDefaultShareMenuItem()
    }

    return builder.build().intent.apply {
      // We cannot use the builder's method enableUrlBarHiding, because there is
      // no corresponding disable method and some browsers enable it by default.
      putExtra(CustomTabsIntent.EXTRA_ENABLE_URLBAR_HIDING, options.enableBarCollapsing)

      val packageName = options.browserPackage
      if (!TextUtils.isEmpty(packageName)) {
        setPackage(packageName)
      }

      if (options.shouldCreateTask) {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        if (!options.showInRecents) {
          addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
          addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        }
      }
    }
  }

  /**
   * @throws NoPreferredPackageFound
   */
  private fun givenOrPreferredPackageName(packageName: String?): String {
    val resolvedPackageName: String? = try {
      packageName?.takeIf { it.isNotEmpty() }.ifNull {
        customTabsResolver.getPreferredCustomTabsResolvingActivity(null)
      }
    } catch (ex: CurrentActivityNotFoundException) {
      throw NoPreferredPackageFound()
    } catch (ex: PackageManagerNotFoundException) {
      throw NoPreferredPackageFound()
    }

    return resolvedPackageName?.takeIf { it.isNotEmpty() }
      ?: throw NoPreferredPackageFound()
  }

  override fun getName(): String = MODULE_NAME
}
