package io.github.lv.ui

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.I18NBundle
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.XmlReader
import com.github.czyzby.autumn.annotation.Component
import com.github.czyzby.autumn.annotation.Inject
import com.kotcrab.vis.ui.layout.GridGroup
import com.kotcrab.vis.ui.widget.*
import io.github.lv.entity.gameUnit.component.UnitInformationComponent
import io.github.lv.io.github.lv.ui.UiDrawables
@Component
class MapUI {
    @Inject
    private lateinit var actionRegistry:ActionRegistry
    var stage: Stage? = null
    var bundle: I18NBundle? = null
    private lateinit var worldPlaceholder: VisTable
    private lateinit var group: GridGroup
    fun clearWorldPlaceholder() {
        worldPlaceholder.clear()
    }

    val uiXmlPath = "config/uiConfig/mapScreen/"
    fun initializeUI(uiStage: Stage, unitPortrait: Image, selectedUnit: Entity?) {
        // 根据需要选择语言
        val languageCode = "en"  // 或 "zh" 用来切换语言
//    val bundle = I18NBundle.createBundle(createBundle("language/menu", Locale("en", "US")))
        bundle = I18NBundle.createBundle(Gdx.files.internal("language/menu_en"))

//        Gdx.input.inputProcessor = uiStage
        // 你可以换成自己的 skin.json；这里用最省事的方式：自己塞 drawables
//        skin = Skin(Gdx.files.internal("uiskin.json"))
//        skin.add("default-font", font)
//
//        // 统一的默认 Label 样式
//        skin.add("default", Label.LabelStyle(skin.getFont("default-font"), Color.WHITE))
//        skin.add("default", TextButton.TextButtonStyle().apply {
//            font = skin.getFont("default-font")
//            up = TextureRegionDrawable(TextureRegion(Texture("ui_btn_up.png")))
//            down = TextureRegionDrawable(TextureRegion(Texture("ui_btn_down.png")))
//        })
        // 一些贴图占位（你自己改文件名即可）

//        VisUI.load(skin)
        // 根表：全屏
        val root = VisTable()
        root.setFillParent(true)

        // ===== 顶栏 =====
        val topBar = VisTable()
//        topBar.background = topBarBg
        topBar.background = colorDrawable(Color(0f, 0f, 0f, 0.6f)) // 半透明黑
//    val menuBar = MenuBar()
//    menuBar.addMenu(createMenu(bundle))
//    menuBar.addMenu(createArchitect(bundle))
        val menuBar = loadMenuBarFromFile("MenuBar.xml")
        topBar.add(menuBar.table)
// 创建菜单项
//        root.add(menuBar.table).growX().height(36f).colspan(2).row()
        // 资源条（随便放几个 label 做占位）
        val resRow = VisTable()
        resRow.add(VisLabel("⚔ 1")).padRight(12f)
        resRow.add(VisLabel("💰 0")).padRight(12f)
        resRow.add(VisLabel("🏠 0/2")).padRight(12f)
        resRow.add(VisLabel("👤 6")).padRight(12f)
        resRow.add(VisLabel("⛏ -4(4)")).padRight(12f)
        resRow.add(VisLabel("✚ 0")).padRight(12f)
        resRow.add(VisLabel("📘 40%")).padRight(12f)
        resRow.add(VisLabel("⏱ 17:17")).padRight(12f)

        topBar.add(resRow).expandX().left().padLeft(16f)
        // ===== 右侧面板 =====
        val rightPanel = VisTable()
        rightPanel.top().right()
//        rightPanel.background = rightPanelBg
        rightPanel.background = colorDrawable(Color(255f, 255f, 0f, 0.6f)) // 半透明黑

        // 头像框
        val uiDrawables = UiDrawables()
        unitPortrait.setScaling(Scaling.fit)
        unitPortrait.drawable = uiDrawables.portraitImg // 可选


        // 小地图下方一排按钮（用 ImageButton 更像图标）
        fun iconButton(name: String): ImageButton {
            // 你用自己的图：ui_icon_xxx.png
            val up = uiDrawables.icon(name)
            val style = ImageButton.ImageButtonStyle().apply { imageUp = up }
            return ImageButton(style)
        }

        val iconRow = VisTable()
//        iconRow.add(iconButton("search")).size(26f).pad(2f)
//        iconRow.add(iconButton("shield")).size(26f).pad(2f)
//        iconRow.add(iconButton("home")).size(26f).pad(2f)
//        iconRow.add(iconButton("people")).size(26f).pad(2f)
//        iconRow.add(iconButton("grid")).size(26f).pad(2f)
//        iconRow.add(iconButton("gear")).size(26f).pad(2f)
        // 坐标/地形信息
        val infoLine1 = VisLabel("21,49")
        val infoLine2 = VisLabel("山岭 (山岭)")
        // 横条 banner（你图里那个夕阳条）
        val banner = VisImage(uiDrawables.sunsetImg)
        banner.setScaling(Scaling.stretch)

        // 头像框 + 右侧属性文字
//        val portrait = Image(uiDrawables.portraitImg)
//        portrait.setScaling(Scaling.fit)
        val stats = VisTable()
        stats.add(VisLabel("name: ${selectedUnit?.getComponent(UnitInformationComponent::class.java)?.name}")).left()
            .row()
        stats.add(VisLabel("health")).left().padTop(8f).row()
        stats.add(VisLabel("experience")).left().padTop(8f).row()
//        stats.add(Label("age${selectedUnit?.age}", skin)).left().padTop(8f).row()
        stats.add(VisLabel("aaa")).left().padTop(8f).row()

//        val unitRow = Table()
//        unitRow.add(portrait).size(72f, 72f).padRight(8f)
//        unitRow.add(stats).expandX().left()

        // 底部大按钮
//        val endTurnStyle = VisTextButton.VisTextButtonStyle().apply {
//            font = skin.getFont("default-font")
//            up = uiDrawables.bottomBtnUp
//            down = uiDrawables.bottomBtnDown
//            fontColor = Color.WHITE
//        }
        val endTurnBtn = VisTextButton("end")
        // 右侧面板拼装
        rightPanel.add(unitPortrait).width(220f).height(180f).pad(8f).row()
        rightPanel.add(iconRow).padLeft(8f).padRight(8f).left().row()
        rightPanel.add(infoLine1).padLeft(10f).padTop(6f).left().row()
        rightPanel.add(infoLine2).padLeft(10f).padTop(2f).left().row()
        rightPanel.add(banner).width(220f).height(40f).pad(8f).row()
        rightPanel.add(stats).padLeft(8f).padRight(8f).left().row()
        rightPanel.add().expandY().row() // 中间留空，撑开到下面
        rightPanel.add(endTurnBtn).width(220f).height(44f).pad(10f).bottom()
        worldPlaceholder = VisTable()

        // ===== 根布局拼装：顶栏一行，下面一行(世界 + 右侧面板) =====
        root.add(topBar).growX().height(36f).colspan(2).row()
        root.add(worldPlaceholder).expand().fill()          // 世界区域吃掉所有剩余空间
        root.add(rightPanel).width(240f).growY().right().top()
        uiStage.addActor(root)
        stage = uiStage
    }

    fun openArchitectMenu(fileName: String?) {
        val xmlReader = XmlReader()
        val xmlFileName = fileName?.removeSuffix(".xml") + ".xml"
        group = GridGroup(72f, 8f); //item size 64px, spacing 8px
//        println(uiXmlPath + "architect/" + xmlFileName)
        val file = Gdx.files.internal(uiXmlPath + "architect/" + fileName + ".xml")
        val rootElement = xmlReader.parse(file)
        for (itemElement in rootElement.children) {
          val   uiDrawable= UiDrawables()
            val imageBtn = VisImageTextButton(itemElement.text, uiDrawable.portraitImg)
            imageBtn.setOrientation(VisImageTextButton.Orientation.TEXT_BOTTOM)
// Create the button container (it can be an ImageButton, but you can use the label for interaction)
            imageBtn.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    // Add your click handling code here

                }
            })

//            label.setWrap(true) // 允许标签换行


            group.addActor(imageBtn)
        }
        // 清空当前内容
        worldPlaceholder.clear()
        // 创建弹出菜单
        worldPlaceholder.add(group).top().left().expand().fill().row()
    }

    fun loadMenuBarFromFile(fileName: String): MenuBar {
        val menuBar = MenuBar()
        val xmlReader = XmlReader()
        val xmlFileName = fileName.removeSuffix(".xml") + ".xml"
        val file = Gdx.files.internal(uiXmlPath + xmlFileName)
        val rootElement = xmlReader.parse(file)
        for (itemElement in rootElement.children) {
            val menu = loadMenuFromFile(itemElement.text)
            menuBar.addMenu(menu)
        }
        return menuBar
    }

    fun loadMenuFromFile(fileName: String): Menu {
        val xmlReader = XmlReader()
        val xmlFileName = fileName.removeSuffix(".xml") + ".xml"
        val file = Gdx.files.internal(uiXmlPath + xmlFileName)
        val rootElement = xmlReader.parse(file)
        // 解析菜单节点
        val menu = Menu(bundle?.get(rootElement.getChildByName("name").text))
//    val menu = Menu(rootElement.getChildByName("name").text)
        val itemsElement = rootElement.getChildByName("items")
        for (itemElement in itemsElement.children) {
            val name = itemElement.getChildByName("name").text
            val menuItem = MenuItem(name)
//        val menuItem = MenuItem(bundle.get(itemElement.getChildByName("name").text))
            val action = itemElement.getChildByName("action")
            menuItem.addListener(createMenuItemListener(action?.text ?: "", name))
            // 如果有子菜单，则加载
            val subMenuElement = itemElement.getChildByName("subMenu")
            if (subMenuElement != null) {
                val subMenu = loadPopupMenuFromFile(subMenuElement.text)
                menuItem.setSubMenu(subMenu)
            }
            menu.addItem(menuItem)
        }
        return menu
    }

    fun loadPopupMenuFromFile(fileName: String): PopupMenu {
        val xmlReader = XmlReader()
        val xmlFileName = fileName.removeSuffix(".xml") + ".xml"
        val file = Gdx.files.internal(uiXmlPath + xmlFileName)
        val rootElement = xmlReader.parse(file)
        // 解析菜单节点
        val menu = PopupMenu()
        val itemsElement = rootElement.getChildByName("items")
        for (itemElement in itemsElement.children) {
            val name = itemElement.getChildByName("name").text
            val menuItem = MenuItem(name)
//        val menuItem = MenuItem(bundle.get(itemElement.getChildByName("name").text))
            val action = itemElement.getChildByName("action")
            menuItem.addListener(createMenuItemListener(action?.text ?: "", name))
            // 如果有子菜单，则递归加载
            val subMenuElement = itemElement.getChildByName("subMenu")
            if (subMenuElement != null) {
                val subMenu = loadPopupMenuFromFile(subMenuElement.text)
                menuItem.setSubMenu(subMenu)
            }
            menu.addItem(menuItem)
        }
        return menu
    }
    // 菜单项点击监听器
    fun createMenuItemListener(action: String, param: Any? = null): ChangeListener {
// 使用 DSL 风格的注册
        val actions = actionRegistry.apply {
            register("newFile") {
                newFileHandler()
            }
            register<String>("openArchitectMenu") { param ->
                this@MapUI.openArchitectMenu(param)
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

}

fun colorDrawable(color: Color): Drawable {
    val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
    pixmap.setColor(color)
    pixmap.fill()
    val texture = Texture(pixmap)
    pixmap.dispose()
    return TextureRegionDrawable(TextureRegion(texture))
}

/*
* 这里用返回object的函数充当组件
* */

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


fun createMenu(bundle: I18NBundle): Menu {
    return object : Menu(bundle.get("Menu")) {
        // 匿名内部类
        init {
            // 创建菜单项
            val newItem = MenuItem("Menu")
            newItem.addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
//                println("新建")
                }
            })

            val openItem = MenuItem("construct")
            openItem.addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
//                println("打开")
                }
            })
            val Menu2 = PopupMenu()
            val wood = MenuItem("wood")
            Menu2.addItem(wood)
            val saveItem = MenuItem("save")
            saveItem.setSubMenu(Menu2)
            saveItem.addListener(object : ChangeListener() {
                override fun changed(event: ChangeListener.ChangeEvent?, actor: Actor?) {
//                println("保存")
                }
            })
            // 将菜单项添加到菜单中
            addItem(newItem)
            addItem(openItem)
            addItem(saveItem)
        }
    }
}

fun createArchitect(bundle: I18NBundle): Menu {
    return object : Menu(bundle.get("Architect")) {
        // 匿名内部类
        init {
            // 创建菜单项
            val newItem = MenuItem(bundle.get("Orders"))
            newItem.addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
//                println("新建")
                }
            })

            // 将菜单项添加到菜单中
            addItem(newItem)

        }
    }
}

fun createArchitectPop(bundle: I18NBundle): PopupMenu {
    return object : PopupMenu(bundle.get("Architect")) {
        // 匿名内部类
        init {
            // 创建菜单项
            val newItem = MenuItem(bundle.get("Orders"))
            newItem.addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
//                println("新建")
                }
            })

            val openItem = MenuItem(bundle.get(""))
            openItem.addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
//                println("打开")
                }
            })
            val Menu2 = PopupMenu()
            val wood = MenuItem("wood")
            Menu2.addItem(wood)
            val saveItem = MenuItem("save")
            saveItem.setSubMenu(Menu2)
            saveItem.addListener(object : ChangeListener() {
                override fun changed(event: ChangeListener.ChangeEvent?, actor: Actor?) {
//                println("保存")
                }
            })
            // 将菜单项添加到菜单中
            addItem(newItem)
            addItem(openItem)
            addItem(saveItem)
        }
    }
}
