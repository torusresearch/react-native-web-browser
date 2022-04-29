package com.reactnativewebbrowser

import android.app.Activity

interface ActivityProvider {
  fun getCurrentActivity(): Activity?
}
