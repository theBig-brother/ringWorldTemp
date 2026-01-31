package io.github.lv.screen

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.XmlReader
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.github.czyzby.autumn.annotation.Component
import com.github.czyzby.autumn.annotation.Initiate
import com.github.czyzby.autumn.annotation.Inject
import io.github.lv.GameResources
import io.github.lv.ecs.EngineContainer
import io.github.lv.ecs.EngineSystem
import io.github.lv.ecs.pawn.PawnBuilder
import io.github.lv.ecs.pawn.system.render.PawnRenderSystem
import io.github.lv.ecs.thing.ThingBuilder
import io.github.lv.tileMap.MapManager
import io.github.lv.tileMap.inputProcessor.MapInputProcessor
import io.github.lv.tileMap.inputProcessor.MouseStateMachine
import io.github.lv.tileMap.inputProcessor.OrderInputProcessor
import io.github.lv.tileMap.inputProcessor.PawnInputProcessor
import io.github.lv.ui.MapUI

@Component
class MapScreen : ScreenAdapter() {
    @Inject
    private lateinit var gameResources: GameResources

    var music: Music? = null

    @Inject
    lateinit var engineContainer: EngineContainer

    @Inject
    private lateinit var mapUI: MapUI

    // 直接注入组件化后的输入处理器
    @Inject
    private lateinit var orderInputProcessor: OrderInputProcessor

    @Inject
    private lateinit var pawnInputProcessor: PawnInputProcessor

    @Inject
    private lateinit var mapInputProcessor: MapInputProcessor

    @Inject
    private lateinit var mapManager: MapManager

    @Inject
    private lateinit var mouseStateMachine: MouseStateMachine
    val multiplexer = InputMultiplexer()        // 创建多重输入处理器
    val uiStage: Stage by lazy { Stage(ScreenViewport()) }

    //    val uiStage: Stage by lazy { stage(viewport=ScreenViewport()) }
    var selectedUnit: Entity? = null
    var unitPortrait: Image = Image()      // 先不给 drawable

    @Initiate
    @Suppress("unused")
    fun initialize() {

    }

    fun updateUnitPortrait(entity: Entity?) {
        mapUI.update(entity)

    }

    override fun show() {
//        viewport = ScreenViewport(camera)
//        gameResources=GameResources()
//        tileMap = TileMap(gameResources.camera, gameResources, terrainConfig)
        mapManager.switch("home")

        pawnInputProcessor.configure(
            getSelectedUnit = { selectedUnit }
        ) { unit ->
            selectedUnit = unit
            updateUnitPortrait(unit)
        }
        orderInputProcessor.configure {
            selectedUnit
        }
        multiplexer.addProcessor(uiStage)         // UI舞台优先
        multiplexer.addProcessor(pawnInputProcessor)        // 然后是ui输入
        multiplexer.addProcessor(orderInputProcessor)     // 接着是游戏输入
        multiplexer.addProcessor(mapInputProcessor)        // 最后是地图输入
        Gdx.input.inputProcessor = multiplexer
        uiStage.isDebugAll = true
        mapUI.initializeUI(uiStage)
        findUnits()
        findThings()
    }

    fun findThings() {
        //  查询数据
        val reader = XmlReader()
        val rootElement = reader.parse(Gdx.files.internal("temp/drop.xml"))
        // 获取根节点
//        println("rootElement\n${rootElement}")
        // 解析每个书籍节点

//        val id1 = rootElement.getChildByName("id").text
//        val health1 = rootElement.getChildByName("health").text
//        val texture1 = rootElement.getChildByName("texture").text
//        println("id: ${id1}, health: $health1, texture: $texture1")
        val thingBuilder1 = ThingBuilder.Builder().apply {
            id = rootElement.getChildByName("id").text.toInt()
            name = rootElement.getChildByName("name").text
            health = rootElement.getChildByName("health").text.toFloat()
            texturePath = rootElement.getChildByName("texture").text
        }.build()
        val querySql = "SELECT id, name, health,unitTexture,mapX,mapY,unitType,tileMapId FROM things"
        gameResources.conn.createStatement().use { stmt ->
            val rs = stmt.executeQuery(querySql)
            while (rs.next()) {
                val thingBuilder = ThingBuilder.Builder().apply {
                    id = rs.getInt("id")
                    name = rs.getString("name")
                    health = rs.getFloat("health")
                    texturePath = rs.getString("unitTexture")
                    tileMap = mapManager.get(rs.getString("tileMapId"))?.tileMap
                    mapX = rs.getInt("mapX")
                    mapY = rs.getInt("mapY")
                    thingUnitType = enumValueOf(rs.getString("unitType"))
                    drop.add(thingBuilder1)
                }.build()
                engineContainer.createThingEntity(
                    thingBuilder
                )
            }
        }
    }

    fun findUnits() {
        //  查询数据
        val querySql = "SELECT id, name, age,unitTexture,mapX,mapY,tileMapId FROM gameUnits"
        gameResources.conn.createStatement().use { stmt ->
            val rs = stmt.executeQuery(querySql)
            while (rs.next()) {
                val pawnBuilder = PawnBuilder.Builder().apply {
                    id = rs.getInt("id")
                    name = rs.getString("name")
                    age = rs.getInt("age")
                    texturePath = rs.getString("unitTexture")
                    tileMap = mapManager.get(rs.getString("tileMapId"))?.tileMap
                    mapX = rs.getInt("mapX")
                    mapY = rs.getInt("mapY")
                }.build()
                engineContainer.createPawnEntity(
                    pawnBuilder
                )
            }
        }
        unitPortrait.setScaling(Scaling.fit)
//        selectedUnit = units[0] // 默认选中一个（可选）
//        updateUnitPortrait(selectedUnit)

    }

    override fun render(delta: Float) {
        // 清空屏幕
        ScreenUtils.clear(Color.GRAY)
        gameResources.camera.update()
        gameResources.batch.projectionMatrix = gameResources.viewport.camera.combined
        mapManager.draw(delta)
        val pawnRenderSystem = engineContainer.pawnEngine.getSystem(PawnRenderSystem::class.java)
        pawnRenderSystem.debugMode = true
        engineContainer.update(delta)

        //在每帧渲染stage
        uiStage.act(Gdx.graphics.deltaTime.coerceAtMost(1 / 30f)) // 更新场景
        uiStage.draw()  // 绘制场景
        mouseStateMachine.update()
    }

    override fun resize(width: Int, height: Int) {
        // 更新stage视图
        uiStage.viewport?.update(width, height, true)
        gameResources.viewport.update(width, height, true)
    }


//    private fun addListener(listener: io.github.lv.screen.`<anonymous>`) {}

    fun showDialog(dialogStage: Stage) {
        // 保存原来的处理器
        val previousProcessor = Gdx.input.inputProcessor


        // 只让对话框接收输入
        Gdx.input.inputProcessor = dialogStage


        // 对话框关闭后恢复
        // Gdx.input.setInputProcessor(previousProcessor);
    }

    override fun hide() {
        Gdx.input.inputProcessor = null  // 完全清除输入处理器
    }

    override fun pause() {
        // 暂停时禁止所有输入
        Gdx.input.inputProcessor = uiStage
    }

    override fun resume() {
        // 恢复时重新设置
        Gdx.input.inputProcessor = multiplexer
    }

    override fun dispose() {
        uiStage.dispose()
        Gdx.input.inputProcessor = null
    }
}
