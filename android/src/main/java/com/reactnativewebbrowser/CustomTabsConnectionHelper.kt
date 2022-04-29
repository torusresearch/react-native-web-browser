package com.reactnativewebbrowser

import android.net.Uri

interface CustomTabsConnectionHelper {
  fun warmUp(packageName: String)
  fun mayInitWithUrl(packageName: String, uri: Uri)
  fun coolDown(packageName: String): Boolean
}
