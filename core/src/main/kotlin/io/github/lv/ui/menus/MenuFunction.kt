package io.github.lv.ui.menus

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.github.czyzby.autumn.annotation.Component
import com.github.czyzby.autumn.annotation.Inject
import com.kotcrab.vis.ui.widget.Menu
import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu
import io.github.lv.GameResources
import io.github.lv.entity.EngineContainer
import io.github.lv.ui.MapUI.Companion.worldPlaceholder
import io.github.lv.ui.menus.MenuManager.Companion.bundle
import io.github.lv.ui.menus.MenuManager.Companion.uiXmlPath
import io.github.lv.ui.menus.MenuManager.Companion.xmlReader

@Component
class MenuFunction {
    fun createMenuFromFile(
        fileName: String,
        createMenuItemListener: (action: String, param: Any?) -> ChangeListener
    ): Menu {
        val xmlFileName = fileName.removeSuffix(".xml") + ".xml"
        val file = Gdx.files.internal(uiXmlPath + xmlFileName)
        val rootElement = xmlReader.parse(file)
        // 解析菜单节点
        val menu = Menu(bundle.get(rootElement.getChildByName("name").text))
        menu.openButton.addListener(
            object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    // 清空当前内容
                    worldPlaceholder.clear()
                }
            }
        )
        val itemsElement = rootElement.getChildByName("items")
        for (itemElement in itemsElement.children) {
            val name = itemElement.getChildByName("name").text
            val menuItem = MenuItem(name)
//    TODO    val menuItem = MenuItem(bundle.get(itemElement.getChildByName("name").text))
            val action = itemElement.getChildByName("action")
            menuItem.addListener(createMenuItemListener(action?.text ?: "", name))
            // 如果有子菜单，则加载
            val subMenuElement = itemElement.getChildByName("subMenu")
            if (subMenuElement != null) {
                val subMenu = createPopupMenuFromFile(subMenuElement.text, createMenuItemListener)
                menuItem.setSubMenu(subMenu)
            }
            menu.addItem(menuItem)
        }
        return menu
    }

    fun createPopupMenuFromFile(
        fileName: String,
        createMenuItemListener: (action: String, param: Any?) -> ChangeListener
    ): PopupMenu {
        val xmlFileName = fileName.removeSuffix(".xml") + ".xml"
        val file = Gdx.files.internal(uiXmlPath + xmlFileName)
        val rootElement = xmlReader.parse(file)
        // 解析菜单节点
        val menu = PopupMenu()
        val itemsElement = rootElement.getChildByName("items")
        for (itemElement in itemsElement.children) {
            val name = itemElement.getChildByName("name").text
            val menuItem = MenuItem(name)
//      TODO   val menuItem = MenuItem(bundle.get(itemElement.getChildByName("name").text))
            val action = itemElement.getChildByName("action")
            menuItem.addListener(createMenuItemListener(action?.text ?: "", name))
            // 如果有子菜单，则递归加载
            val subMenuElement = itemElement.getChildByName("subMenu")
            if (subMenuElement != null) {
                val subMenu = createPopupMenuFromFile(subMenuElement.text, createMenuItemListener)
                menuItem.setSubMenu(subMenu)
            }
            menu.addItem(menuItem)
        }
        return menu
    }
}
