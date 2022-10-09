package com.reactnativewebbrowser

import android.content.Intent
import android.net.Uri
import java.util.ArrayList


interface CustomTabsActivitiesHelper {
  val customTabsResolvingActivities: ArrayList<String>

  val customTabsResolvingServices: ArrayList<String>

  fun getPreferredCustomTabsResolvingActivity(packages: List<String>?): String?

  val defaultCustomTabsResolvingActivity: String?

  fun startCustomTabs(intent: Intent, url: Uri?)

  fun canResolveIntent(intent: Intent): Boolean
}
