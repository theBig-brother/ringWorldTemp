package io.github.lv.ui.menus

import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.kotcrab.vis.ui.widget.Menu

interface MenuBase {
    var menuFunction: MenuFunction
    var menu: Menu
    var actionRegistry: ActionRegistry
    fun createMenuItemListener(action: String, param: Any? = null): ChangeListener
}
