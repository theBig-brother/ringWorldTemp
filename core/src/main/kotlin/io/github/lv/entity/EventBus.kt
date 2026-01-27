package io.github.lv.entity

object EventBus {
    private val listeners = mutableListOf<(Any) -> Unit>()

    fun addListener(listener: (Any) -> Unit) {
        listeners.add(listener)
    }

    fun postEvent(event: Any) {
        listeners.forEach { it(event) }
    }
}
