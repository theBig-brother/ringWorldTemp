package io.github.lv.ui.menus

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.github.czyzby.autumn.annotation.Component


@Component
class ActionRegistry {
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
* 它是一个命令路由机制，把 UI 事件解耦成字符串动作并映射到具体处理函数。

如果你愿意我可以进一步帮你：

✔ 把这套升级成 RimWorld CommandAction
✔ 分菜单域（Architect、Orders、Build）
✔ 分 EditorState
✔ 支持 Tile/Pawn 的上下文
✔ 加 Undo/Redo
✔ 或给出完整架构图（ECS + Menu Command + Selection）

你继续问一句就行。
* */
