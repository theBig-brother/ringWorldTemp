package io.github.lv.ui

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.czyzby.autumn.annotation.Component
import com.github.czyzby.autumn.annotation.Inject
import com.kotcrab.vis.ui.building.OneRowTableBuilder
import com.kotcrab.vis.ui.building.utilities.Padding
import com.kotcrab.vis.ui.layout.GridGroup
import com.kotcrab.vis.ui.widget.VisTable
import io.github.lv.ui.component.ResRow
import io.github.lv.ui.component.RightPanel
import io.github.lv.ui.menus.MenuManager
import io.github.lv.ui.somethingElse.ColorDrawable
import ktx.scene2d.actor
import ktx.scene2d.actors
import ktx.scene2d.vis.menuBar
import ktx.scene2d.vis.visTable


@Component
class MapUI {
    @Inject
    private lateinit var menuManager: MenuManager

    @Inject
    private lateinit var rightPanel: RightPanel

    @Inject
    private lateinit var resRow: ResRow

    companion object {
        val root = VisTable()
        val worldPlaceholder: VisTable = VisTable()
        val architectureGroup: GridGroup = GridGroup(72f, 8f)

        //菜单
//        val menuBar = MenuBar()

        //其他按钮
        val btnRowBuilder = OneRowTableBuilder(Padding(2f, 0f))
    }

    object rightInfo {

    }

    //    object leftInfo{
//
//    }
    var stage: Stage? = null

    fun clearWorldPlaceholder() {
        worldPlaceholder.clear()
    }

    fun rightPanelShow(i: Boolean) {
        if (i) {
//            root.add(rightPanel).width(240f).growY().right().top()
            rightPanel.actor.isVisible = true
        } else {
//            rightPanel.remove()
            rightPanel.actor.isVisible = false
//            root.removeActor(rightPanel)
//            root.invalidateHierarchy()
        }
    }

    fun initializeUI(uiStage: Stage) {
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

        // ===== 根布局拼装：顶栏一行，下面一行(世界 + 右侧面板) =====
        // 根表：全屏
        uiStage.actors {
            visTable {
                setFillParent(true)
                // ===== 顶栏 =====
                visTable { cell ->
                    background = ColorDrawable(Color(0f, 0f, 0f, 0.6f)) // 半透明黑
                    cell.growX().height(36f).colspan(2).row()
// 菜单项
                    menuBar {
                        addMenu(menuManager.settingMenu.menu)
                        addMenu(menuManager.architectMenu.menu)
                        addMenu(menuManager.otherMenu.menu)
                        //TODO   menuBar.addMenu(createMenuFromFile("Mods"))
                    }
                    // 放按钮行
                    actor(btnRowBuilder.build())
                    // 放资源行
                    actor(resRow.actor) {
                        it.expandX().left().padLeft(16f)
                    }
                }
                // ===== 世界区域 =====
                actor(worldPlaceholder) {
                    // 世界区域吃掉所有剩余空间
                    it.expand().fill()
                }

                actor(rightPanel.actor) {
                    it.width(240f).growY().right().top()
                }
            }
        }
        rightPanel.actor.remove()
        stage = uiStage
    }

    fun update(selectedUnit: Entity?) {
        resRow.update()
        rightPanel.update(selectedUnit)
    }
}


/*
* 这里用返回object的函数充当组件
* */
