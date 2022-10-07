package com.reactnativewebbrowser.modules

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Field(val key: String = "")
