package com.reactnativewebbrowser

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import com.facebook.react.bridge.LifecycleEventListener

class InternalCustomTabsConnectionHelper internal constructor(private val context: Context) :
  CustomTabsServiceConnection(), LifecycleEventListener,
  CustomTabsConnectionHelper {
  private var mPackageName: String? = null
  private val clientActions: DeferredClientActionsQueue<CustomTabsClient> =
    DeferredClientActionsQueue()
  private val sessionActions: DeferredClientActionsQueue<CustomTabsSession> =
    DeferredClientActionsQueue()

  override fun warmUp(packageName: String) {
    clientActions.executeOrQueueAction { client -> client.warmup(0) }
    ensureConnection(packageName)
  }

  override fun mayInitWithUrl(packageName: String, uri: Uri) {
    sessionActions.executeOrQueueAction { session ->
      session.mayLaunchUrl(
        uri,
        null,
        null
      )
    }
    ensureConnection(packageName)
    ensureSession()
  }

  private fun ensureSession() {
    if (!sessionActions.hasClient()) {
      clientActions.executeOrQueueAction { client ->
        sessionActions.setClient(
          client.newSession(null)
        )
      }
    }
  }

  override fun coolDown(packageName: String): Boolean {
    if ((packageName == mPackageName)) {
      unbindService()
      return true
    }
    return false
  }

  private fun ensureConnection(packageName: String) {
    if (mPackageName != null && !(mPackageName == packageName)) {
      clearConnection()
    }
    if (!connectionStarted(packageName)) {
      CustomTabsClient.bindCustomTabsService(context, packageName, this)
      mPackageName = packageName
    }
  }

  private fun connectionStarted(packageName: String): Boolean {
    return (packageName == mPackageName)
  }

  override fun onBindingDied(componentName: ComponentName) {
    if ((componentName.packageName == mPackageName)) {
      clearConnection()
    }
  }

  override fun onCustomTabsServiceConnected(
    componentName: ComponentName,
    client: CustomTabsClient
  ) {
    if ((componentName.packageName == mPackageName)) {
      clientActions.setClient(client)
    }
  }

  override fun onServiceDisconnected(componentName: ComponentName) {
    if ((componentName.packageName == mPackageName)) {
      clearConnection()
    }
  }

  override fun onHostResume() {
    // do nothing
  }

  override fun onHostPause() {
    // do nothing
  }

  override fun onHostDestroy() {
    unbindService()
  }

  private fun unbindService() {
    context.unbindService(this)
    clearConnection()
  }

  private fun clearConnection() {
    mPackageName = null
    clientActions.clear()
    sessionActions.clear()
  }

}
