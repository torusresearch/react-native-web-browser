package com.reactnativewebbrowser.modules

data class OpenBrowserOptions(
  @Field var toolbarColor: Int? = null,
  @Field var secondaryToolbarColor: Int? = null,
  @Field var browserPackage: String? = null,
  @Field var showTitle: Boolean = false,
  @Field var enableDefaultShareMenuItem: Boolean = false,
  @Field var enableBarCollapsing: Boolean = false,
  @Field var showInRecents: Boolean = false,
  @Field(key = "createTask") var shouldCreateTask: Boolean = true
) : Record
