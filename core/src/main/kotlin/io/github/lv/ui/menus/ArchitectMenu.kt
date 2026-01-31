package io.github.lv.ui.menus

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.github.czyzby.autumn.annotation.Component
import com.github.czyzby.autumn.annotation.Initiate
import com.github.czyzby.autumn.annotation.Inject
import com.kotcrab.vis.ui.widget.Menu
import com.kotcrab.vis.ui.widget.VisImageTextButton
import io.github.lv.GameResources
import io.github.lv.ecs.thing.ThingBuilder
import io.github.lv.ecs.thing.component.ThingUnitType
import io.github.lv.ui.somethingElse.GameAssets
import io.github.lv.ui.somethingElse.UiDrawables
import io.github.lv.tileMap.ConstructPlan
import io.github.lv.tileMap.inputProcessor.MouseState
import io.github.lv.tileMap.inputProcessor.MouseStateMachine
import io.github.lv.ui.MapUI.Companion.architectureGroup
import io.github.lv.ui.MapUI.Companion.worldPlaceholder
import io.github.lv.ecs.pawn.state.OrderState
import io.github.lv.ecs.zone.ZoneBuilder
import io.github.lv.tileMap.MapManager
import io.github.lv.ui.menus.MenuManager.Companion.uiXmlPath
import io.github.lv.ui.menus.MenuManager.Companion.xmlReader
import ktx.actors.*

@Component
class ArchitectMenu : MenuBase {
    @Inject
    override lateinit var menuFunction: MenuFunction

    @Inject
    private lateinit var mapManager: MapManager

    @Inject
    private lateinit var mouseStateMachine: MouseStateMachine

    @Inject
    private lateinit var gameResources: GameResources

    @Inject
    private lateinit var constructPlan: ConstructPlan

    @Inject
    override lateinit var actionRegistry: ActionRegistry

    override lateinit var menu: Menu

    @Initiate
    fun init() {
        menu = menuFunction.createMenuFromFile("ArchitectMenu", ::createMenuItemListener)
    }

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

    //TODO不要在when里面写所有逻辑,也不要运行的时候再读取xml
    fun openArchitectMenu(fileName: String?) {
        val xmlFileName = fileName?.removeSuffix(".xml") + ".xml"
//        println(uiXmlPath + "architect/" + xmlFileName)
        val file = Gdx.files.internal(uiXmlPath + "architect/" + xmlFileName)
        val rootElement = xmlReader.parse(file)
        architectureGroup.clear()
        when (xmlFileName) {
            "Orders.xml" -> {
                for (itemElement in rootElement.children) {
                    val texture = itemElement.getChildByName("texture").text
                    val name = itemElement.getChildByName("name").text
                    val imageBtn = VisImageTextButton(name, GameAssets.drawable(texture))
                    imageBtn.setOrientation(VisImageTextButton.Orientation.TEXT_BOTTOM)
// Create the button container (it can be an ImageButton, but you can use the label for interaction)
                    imageBtn.onClick {
                        // Add your click handling code here
                        gameResources.orderState = OrderState.valueOf(name)
                        mouseStateMachine.mouseStateMachine.changeState(MouseState.ORDER_RECT)
                    }
//            label.setWrap(true) // 允许标签换行
                    architectureGroup.addActor(imageBtn)
                }
            }

            "Zone.xml" -> {
                for (itemElement in rootElement.children) {
                    val uiDrawable = UiDrawables()
                    val zoneType = itemElement.getChildByName("ZoneType").text
                    val name = itemElement.getChildByName("name").text
                    val texture = itemElement.getChildByName("texture").text
                    val imageBtn = VisImageTextButton(zoneType, uiDrawable.portraitImg)
                    imageBtn.setOrientation(VisImageTextButton.Orientation.TEXT_BOTTOM)
                    imageBtn.onClick {
                        mouseStateMachine.mouseStateMachine.changeState(MouseState.ZONE)
                        constructPlan.currentZone = ZoneBuilder.Builder().apply {
                            this@apply.name = name
                            this@apply.id = 1
                            texturePath = texture
                            tileMap=mapManager.current()?.tileMap
                        }.build()
                    }
                    architectureGroup.addActor(imageBtn)
                }
            }

            "Structure.xml" -> {

            }

            "Floors.xml" -> {

            }

            else -> {
                for (itemElement in rootElement.children) {
                    val theRootElement = xmlReader.parse(
                        Gdx.files.internal(
                            uiXmlPath + "architect/" + xmlFileName.removeSuffix(".xml") + "/" + itemElement.text.removeSuffix(
                                ".xml"
                            ) + ".xml"
                        )
                    )
                    val itemName = theRootElement.getChildByName("name").text
                    val itemTexture = theRootElement.getChildByName("texture").text
                    val imageBtn = VisImageTextButton(itemName, GameAssets.drawable(itemTexture))
                    imageBtn.setOrientation(VisImageTextButton.Orientation.TEXT_BOTTOM)
// Create the button container (it can be an ImageButton, but you can use the label for interaction)
                    imageBtn.addListener(object : ClickListener() {
                        override fun clicked(event: InputEvent?, x: Float, y: Float) {
                            // Add your click handling code here
                            mouseStateMachine.mouseStateMachine.changeState(MouseState.CONSTRUCTION)
                            constructPlan.currentThing = ThingBuilder.Builder().apply {
                                this@apply.name = itemName
                                texturePath = itemTexture
                                thingUnitType = ThingUnitType.PLAN

                            }.build()
                        }
                    })
//            label.setWrap(true) // 允许标签换行
                    architectureGroup.addActor(imageBtn)
                }
            }
        }
        // 清空当前内容
        worldPlaceholder.clear()
        // 创建弹出菜单
        worldPlaceholder.add(architectureGroup).top().left().expand().fill().row()
    }

}
