package io.github.lv.ui.menus


import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.github.czyzby.autumn.annotation.Component
import com.github.czyzby.autumn.annotation.Initiate
import com.github.czyzby.autumn.annotation.Inject
import com.kotcrab.vis.ui.layout.GridGroup
import com.kotcrab.vis.ui.widget.*
import io.github.lv.GameResources
import io.github.lv.entity.EngineContainer
import io.github.lv.entity.pawn.component.PathComponent
import io.github.lv.entity.pawn.component.PawnAppearanceComponent
import io.github.lv.entity.pawn.component.PawnInformationComponent
import io.github.lv.entity.pawn.component.WorkComponent
import io.github.lv.screen.ResearchScreen
import io.github.lv.ui.MapUI.Companion.worldPlaceholder
import io.github.lv.ui.menus.MenuManager.Companion.uiXmlPath
import io.github.lv.ui.menus.MenuManager.Companion.xmlReader
import io.github.lv.ui.somethingElse.ColorDrawable
import ktx.scene2d.scene2d
import ktx.scene2d.vis.*

@Component
class OtherMenu : MenuBase {
    @Inject
    override lateinit var menuFunction: MenuFunction

    @Inject
    lateinit var engineContainer: EngineContainer

    @Inject
    private lateinit var gameResources: GameResources

    @Inject
    override lateinit var actionRegistry: ActionRegistry

    //    不能Inject
    override lateinit var menu: Menu

    @Inject
    private lateinit var researchScreen: ResearchScreen

    private val unitEngine get() = engineContainer.pawnEngine

    @Initiate
    @Suppress("unused")
    fun init() {
        menu = menuFunction.createMenuFromFile("OtherMenu", ::createMenuItemListener)
    }

    override fun createMenuItemListener(action: String, param: Any?): ChangeListener {
// 使用 DSL 风格的注册
        val actions = actionRegistry.apply {
            register("openPriorityMenu") {
                openPriorityMenu()
            }
            register("openResearchMenu") {
                openResearchMenu()
            }
            register("openScheduleMenu") {
                openScheduleMenu()
            }
            register("openAssignMenu") {
                openAssignMenu()
            }
        }

        return object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                // 根据 action 映射调用相应的处理函数
                actions.dispatch(action, param)
            }
        }
    }

    fun openPriorityMenu() {
        val entities = unitEngine.getEntitiesFor(
            Family.all(PawnAppearanceComponent::class.java, PathComponent::class.java).get()
        )
        val btnSize = 32f
        val fileName = "workType"
        val xmlFileName = fileName.removeSuffix(".xml") + ".xml"
        val file = Gdx.files.internal("$uiXmlPath/$xmlFileName")
        // 初始化 GridGroup (用于网格布局)
        val rootElement = xmlReader.parse(file)

        val priorityRoot = scene2d.visTable {
            background = ColorDrawable(Color(0f, 0f, 0f, 0.6f)) // 半透明黑
            //priorityTitle
            visTable {
                it.padLeft(64f).expandX().fillX().row()
                gridGroup(btnSize * 2, 0f) { it ->
                    itemHeight = 32f
                    it.top().left().expand().fill().row()
                    rootElement.children.forEachIndexed { index, itemElement ->
                        val itemName = itemElement.getChildByName("workType").text
                        val l = VisLabel(itemName, Align.center)
                        if (index % 2 == 0) {
                            addActor(l)
                        }
                    }
                }
                gridGroup(btnSize * 2, 0f) { it ->
                    it.height(btnSize).top().left().expand().fill().padLeft(32f).row()
                    rootElement.children.forEachIndexed { index, itemElement ->
                        val itemName = itemElement.getChildByName("workType").text
                        val l = VisLabel(itemName, Align.center)
                        if (index % 2 == 1) {
                            addActor(l)
                        }
                    }
                    itemHeight = 32f
                }
            }
            //priorityDown
            visTable { cell ->
                cell.expandX().fillX()
                for (entity in entities) {
                    val pawnInformationComponent = entity.getComponent(PawnInformationComponent::class.java)
                    val pawnAppearanceComponent = entity.getComponent(PawnAppearanceComponent::class.java)
                    val workComponent = WorkComponent.mapper[entity]
//                  priorityRow
                    visTable {
                        it.left().expandX().fillX().row()
                        //人物
                        visTable { cell ->
                            cell.width( /* 固定宽度 */ 64f).height(btnSize)
                            visImage(pawnAppearanceComponent.unitSprite.texture).apply {
                                setScaling(Scaling.fit) // 保持宽高比适应容器
                                setSize(btnSize, btnSize)
                            }
                            visLabel(pawnInformationComponent.name).apply {
                                setSize(32f, btnSize)
                                setAlignment(Align.center)
                            }
                        }
                        //优先级
                        gridGroup(btnSize, 0f) { it ->
                            it.padLeft(16f).expand().fill()
                            for (i in 0 until rootElement.children.size) {
                                visTextButton(workComponent.workPriorities[i].priority.toString())
                            }
                        }
                    }
                }
            }
        }
        // 清空当前内容
        worldPlaceholder.clear()
        // 创建弹出菜单
        worldPlaceholder.add(priorityRoot).top().left().expand().fillX()//这里expandX会导致居中
    }

    fun openthePriorityMenu() {
        val entities = unitEngine.getEntitiesFor(
            Family.all(PawnAppearanceComponent::class.java, PathComponent::class.java).get()
        )
        val btnSize = 32f
        val priorityRoot = VisTable()
        //priorityTitle
        val priorityTitle = VisTable().padLeft(64f)
        val fileName = "workType"
        val xmlFileName = fileName.removeSuffix(".xml") + ".xml"
        val file = Gdx.files.internal("$uiXmlPath/$xmlFileName")
        // 初始化 GridGroup (用于网格布局)
        val rootElement = xmlReader.parse(file)
//分2行，worktype太多，一行写不下
        val gridGroupRow1 = GridGroup(btnSize * 2, 0f)
        val gridGroupRow2 = GridGroup(btnSize * 2, 0f)
        gridGroupRow1.itemHeight = 32f
        gridGroupRow2.itemHeight = 32f
        rootElement.children.forEachIndexed { index, itemElement ->
            val itemName = itemElement.getChildByName("workType").text
            val l = VisLabel(itemName, Align.center)
            if (index % 2 == 0) {
                gridGroupRow1.addActor(l)
            } else {
                gridGroupRow2.addActor(l)
            }
        }
        priorityTitle.add(gridGroupRow1).top().left().expand().fill().row()
        priorityTitle.add(gridGroupRow2).top().left().expand().fill().padLeft(32f).row()// 空出 32px 的距离
        //priorityDown
        val priorityDown = VisTable()
        for (entity in entities) {
            val priorityRow = VisTable()
            val pawnInformationComponent = entity.getComponent(PawnInformationComponent::class.java)
            val pawnAppearanceComponent = entity.getComponent(PawnAppearanceComponent::class.java)
//            val gridGroupColumn1 = HorizontalGroup()
//            val gridGroupColumn1 = GridGroup(32f,0f)
            val gridGroupColumn1 = VisTable()
            //Image
            val image = VisImage(pawnAppearanceComponent.unitSprite.texture)
            image.setScaling(Scaling.fit) // 保持宽高比适应容器
            image.setSize(btnSize, btnSize)
//            gridGroupColumn1.addActor(image)
            gridGroupColumn1.add(image)
            //Name
            val name = VisLabel()
            name.setSize(32f, btnSize)
//            name.height=32f
            name.setAlignment(Align.center)
//            name.setFontScale(.5f)
            name.setText(pawnInformationComponent.name)
//            gridGroupColumn1.addActor(name)
            gridGroupColumn1.add(name)

            val gridGroupColumn2 = GridGroup(btnSize, 0f)
            @Suppress("unused")
            for (i in 0 until rootElement.children.size) {
//                gridGroup3.addActor(VisTextButton("1"))
//                val textButton=VisTextButton("1")
//                textButton.height=32f
                gridGroupColumn2.addActor(VisTextButton("1"))
            }
            //                priorityDown
            priorityRow.add(gridGroupColumn1).width( /* 固定宽度 */ 64f).height(btnSize)
            priorityRow.add(gridGroupColumn2).padLeft(16f).expand().fill()
            priorityDown.add(priorityRow).left().expandX().fillX().row()

        }
//            gridGroup.setFillParent(true)  // 填充整个父容器

        //整个 priorityTable
        val priorityTable = VisTable()
//        priorityTable.add(priorityTitle).top().left().expandX().fillX().padLeft(64f).row()
        priorityTable.add(priorityTitle).expandX().fillX().row()
        priorityTable.add(priorityDown).expandX().fillX()
        priorityRoot.background = ColorDrawable(Color(0f, 0f, 0f, 0.6f)) // 半透明黑
        priorityRoot.add(priorityTable).top().left().expandX().fillX()

        // 清空当前内容
        worldPlaceholder.clear()
        // 创建弹出菜单
        worldPlaceholder.add(priorityRoot).top().left().expand().fillX()//这里expandX会导致居中
    }

    fun openScheduleMenu() {
        val schedule = VisTable()
        // 清空当前内容
        worldPlaceholder.clear()
        // 创建弹出菜单
        worldPlaceholder.add(schedule).top().left().expand().fillX()
    }

    fun openResearchMenu() {
        val research = VisTable()
        gameResources.game.setScreen(researchScreen)
        // 清空当前内容
        worldPlaceholder.clear()
        // 创建弹出菜单
        worldPlaceholder.add(research).top().left().expand().fillX()
    }

    fun openAssignMenu() {
        val assign = VisTable()
        // 清空当前内容
        worldPlaceholder.clear()
        // 创建弹出菜单
        worldPlaceholder.add(assign).top().left().expand().fillX()
    }

    fun animals(): VisTextButton {
        val animals = VisTextButton("")
        return animals
    }

    fun Wildlife(): VisTextButton {
        val menu = VisTextButton("")
        return menu
    }

    fun Quests(): VisTextButton {
        val menu = VisTextButton("")
        return menu
    }

    fun World(): VisTextButton {
        val menu = VisTextButton("")
        return menu
    }

    fun Ideology(): VisTextButton {
        val menu = VisTextButton("")
        return menu
    }

    fun History(): VisTextButton {
        val menu = VisTextButton("")
        return menu
    }

    fun Factions(): VisTextButton {
        val menu = VisTextButton("")
        return menu
    }
}
