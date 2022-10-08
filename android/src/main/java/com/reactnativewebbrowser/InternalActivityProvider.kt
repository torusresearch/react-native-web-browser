package com.reactnativewebbrowser

import android.app.Activity
import com.facebook.react.bridge.ReactContext

class InternalActivityProvider(private val reactContext: ReactContext) :
  ActivityProvider {
  override fun getCurrentActivity(): Activity? {
    return reactContext.currentActivity
  }
}
