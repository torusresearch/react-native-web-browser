package com.reactnativewebbrowser.utilities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.browser.customtabs.CustomTabsService
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap

/**
 * Returns receiver, or block result if the receiver is `null`
 *
 * A more semantic equivalent to: `nullable ?: run { ... }`:
 * ```
 * val nonNullable1 = sthNullable.ifNull { ... }
 * val nonNullable2 = sthNullable ?: run { ... }
 * ```
 */
inline fun <T> T?.ifNull(block: () -> T): T = this ?: block()

fun WritableMap.putStringArrayList(name: String, strArr: ArrayList<String>) {
  val arr = Arguments.createArray()
  for (str in strArr) {
    arr.pushString(str)
  }
  this.putArray(name, arr)
}

val ALLOWED_CUSTOM_TABS_PACKAGES =
  arrayOf(
    "com.android.chrome", // Chrome stable
    "com.google.android.apps.chrome", // Chrome system
    "com.chrome.beta",// Chrome beta
    "com.chrome.dev" // Chrome dev
  )

fun Context.getDefaultBrowser(): String? {
  val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://web3auth.io"))
  val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
    ?: return null
  val activityInfo = resolveInfo.activityInfo ?: return null
  return activityInfo.packageName
}

fun Context.getCustomTabsBrowsers(): List<String> {
  val customTabsBrowsers: MutableList<String> = java.util.ArrayList()
  for (browser in ALLOWED_CUSTOM_TABS_PACKAGES) {
    val customTabsIntent = Intent()
    customTabsIntent.action = CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION
    customTabsIntent.setPackage(browser)

    // Check if this package also resolves the Custom Tabs service.
    if (packageManager.resolveService(customTabsIntent, 0) != null) {
      customTabsBrowsers.add(browser)
    }
  }
  return customTabsBrowsers
}
