package com.reactnativewebbrowser

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsService
import java.util.ArrayList
import java.util.Collections
import java.util.LinkedHashSet


import androidx.browser.customtabs.CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION
import com.reactnativewebbrowser.error.CurrentActivityNotFoundException
import com.reactnativewebbrowser.error.PackageManagerNotFoundException
import com.reactnativewebbrowser.utilities.getCustomTabsBrowsers
import com.reactnativewebbrowser.utilities.getDefaultBrowser

class InternalCustomTabsActivitiesHelper(val activityProvider: ActivityProvider) :
  CustomTabsActivitiesHelper {

  override val customTabsResolvingActivities: ArrayList<String>
    get() = mapCollectionToDistinctArrayList(
      getResolvingActivities(
        createDefaultCustomTabsIntent()
      )
    ) { resolveInfo -> resolveInfo.activityInfo.packageName }

  override val customTabsResolvingServices: ArrayList<String>
    get() {
      return mapCollectionToDistinctArrayList(
        packageManager.queryIntentServices(
          createDefaultCustomTabsServiceIntent(),
          0
        )
      ) { resolveInfo -> resolveInfo.serviceInfo.packageName }
    }

  override fun getPreferredCustomTabsResolvingActivity(packages: List<String>?): String? {
    var packages: List<String>? = packages
    if (packages == null) packages = customTabsResolvingActivities
    return CustomTabsClient.getPackageName(currentActivity, packages)
  }

  override val defaultCustomTabsResolvingActivity: String?
    get() {
      val info: ResolveInfo? =
        packageManager.resolveActivity(createDefaultCustomTabsIntent(), 0)
      return info?.activityInfo?.packageName
    }

  override fun canResolveIntent(intent: Intent): Boolean {
    return getResolvingActivities(intent).isNotEmpty()
  }

  override fun startCustomTabs(intent: Intent) {
    val defaultBrowser = currentActivity.getDefaultBrowser()
    val customTabsBrowsers = currentActivity.getCustomTabsBrowsers()

    val url = intent.data
    if (customTabsBrowsers.contains(defaultBrowser)) {
      val customTabs = CustomTabsIntent.Builder().build()
      customTabs.intent.setPackage(defaultBrowser)
      if (url != null) {
        customTabs.launchUrl(currentActivity, url)
      }
    } else if (customTabsBrowsers.isNotEmpty()) {
      val customTabs = CustomTabsIntent.Builder().build()
      customTabs.intent.setPackage(customTabsBrowsers[0])
      if (url != null) {
        customTabs.launchUrl(currentActivity, url)
      }
    } else {
      // Open in browser externally
      currentActivity.startActivity(Intent(Intent.ACTION_VIEW, url))
    }
  }

  private fun getResolvingActivities(intent: Intent): List<ResolveInfo> {
    return packageManager.queryIntentActivities(intent, 0)
  }

  private val packageManager: PackageManager
    get() {
      val pm: PackageManager? = currentActivity.packageManager
      if (pm == null) throw PackageManagerNotFoundException() else return pm
    }

  private val currentActivity: Activity
    get() {
      return activityProvider.getCurrentActivity()
        ?: throw CurrentActivityNotFoundException()
    }

  private fun createDefaultCustomTabsIntent(): Intent {
    val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
    val customTabsIntent: CustomTabsIntent = builder.build()
    val intent: Intent = customTabsIntent.intent
    intent.data = Uri.parse(DUMMY_URL)
    return intent
  }

  private fun createDefaultCustomTabsServiceIntent(): Intent {
    val serviceIntent: Intent = Intent()
    serviceIntent.action = CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION
    return serviceIntent
  }

  fun onDestroy() {}

  companion object {
    private val DUMMY_URL: String = "https://expo.io"
    fun <T, R> mapCollectionToDistinctArrayList(
      toMap: Collection<T>,
      mapper: (T) -> R
    ): ArrayList<R> {
      val resultSet: LinkedHashSet<R> = LinkedHashSet()
      for (element: T in toMap) {
        resultSet.add(mapper(element))
      }
      return ArrayList(resultSet)
    }
  }
}
