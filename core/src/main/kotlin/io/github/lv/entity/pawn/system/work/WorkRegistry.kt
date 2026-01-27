package io.github.lv.entity.pawn.system.work

import com.github.czyzby.autumn.annotation.Component


@Component
class WorkRegistry {
    val handlers = mutableMapOf<String, (Any?) -> Unit>()

    // 注册无参数处理函数
    fun register(name: String, handler: () -> Unit) {
        handlers[name] = { _ -> handler() }
    }

    // 注册带参数处理函数
    inline fun <reified T> register(name: String, handler: (T) -> Unit) {
        handlers[name] = { param -> handler(param as T) }
    }

    fun dispatch(name: String, param: Any? = null) {
        handlers[name]?.invoke(param)
    }

}
/*
// 菜单项点击监听器
override fun createMenuItemListener(action: String, param: Any?): ChangeListener {
// 使用 DSL 风格的注册
    val actions = actionRegistry.apply {
        register<String>("openArchitectMenu") { param ->
            openArchitectMenu(param)
        }
    }
    return object : ChangeListener() {
        override fun changed(event: ChangeEvent?, actor: Actor?) {
            // 根据 action 映射调用相应的处理函数
            actions.dispatch(action, param)
        }
    }
}
*/
