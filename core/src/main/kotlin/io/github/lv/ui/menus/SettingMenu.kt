package io.github.lv.ui.menus

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.github.czyzby.autumn.annotation.Component
import com.github.czyzby.autumn.annotation.Initiate
import com.github.czyzby.autumn.annotation.Inject
import com.kotcrab.vis.ui.widget.Menu

@Component
class SettingMenu : MenuBase {
    @Inject
    override lateinit var menuFunction: MenuFunction
    @Inject
    override lateinit var actionRegistry: ActionRegistry

    override lateinit var menu: Menu
    @Initiate
    fun init() {
        menu = menuFunction.createMenuFromFile("SettingMenu", ::createMenuItemListener)
    }
    override fun createMenuItemListener(action: String, param: Any?): ChangeListener {
        // 使用 DSL 风格的注册
        val actions = actionRegistry.apply {
            register("newFile") {
                newFileHandler()
            }

            register<Pair<String, Boolean>>("saveFile") { (path, overwrite) ->
                saveFileHandler(path, overwrite)
            }
        }
        return object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                // 根据 action 映射调用相应的处理函数
                actions.dispatch(action, param)
            }
        }
    }
    // 事件处理函数
    fun newFileHandler() {
        println("New File clicked!")
    }

    fun saveFileHandler(path: String, overwrite: Boolean) {

    }


    fun openFileHandler(path: String, overwrite: Boolean = false) {
        println("Open File clicked!")
    }

    fun showRecentsHandler() {
        println("Show Recents clicked!")
    }

    fun managePluginsHandler() {
        println("Manage Plugins clicked!")
    }


}

