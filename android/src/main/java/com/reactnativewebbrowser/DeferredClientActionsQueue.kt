package com.reactnativewebbrowser

import java.util.LinkedList
import java.util.Queue


class DeferredClientActionsQueue<T> {
  private val actions: Queue<Consumer<T>> = LinkedList()
  private var client: T? = null
  fun executeOrQueueAction(action: Consumer<T>) {
    client?.also {
      action.apply(it)
    } ?: run {
      addActionToQueue(action)
    }
  }

  fun setClient(client: T) {
    this.client = client
    executeQueuedActions()
  }

  fun clear() {
    client = null
    actions.clear()
  }

  fun hasClient(): Boolean {
    return client != null
  }

  private fun executeQueuedActions() {
    client?.also {
      var action = actions.poll()
      while (action != null) {
        action.apply(it)
        action = actions.poll()
      }
    }
  }

  private fun addActionToQueue(consumer: Consumer<T>) {
    actions.add(consumer)
  }
}
