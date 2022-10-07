package com.reactnativewebbrowser.utilities

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
