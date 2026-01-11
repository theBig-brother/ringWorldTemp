package io.github.lv.screen

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.XmlReader
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.github.czyzby.autumn.annotation.Component
import com.github.czyzby.autumn.annotation.Initiate
import com.github.czyzby.autumn.annotation.Inject
import io.github.lv.GameResources
import io.github.lv.entity.EngineContainer
import io.github.lv.entity.gameUnit.UnitBuilder
import io.github.lv.entity.gameUnit.component.UnitAppearanceComponent
import io.github.lv.entity.gameUnit.system.RenderSystem
import io.github.lv.io.github.lv.ui.GameAssets.texture
import io.github.lv.entity.thing.ThingBuilder
import io.github.lv.tileMap.MapInputProcessor
import io.github.lv.tileMap.MapManager
import io.github.lv.tileMap.TerrainConfig
import io.github.lv.tileMap.TileMap
import io.github.lv.ui.MapUI


@Component
class MapScreen : ScreenAdapter() {
    @Inject
    private lateinit var gameResources: GameResources

    var music: Music? = null

    @Inject
    private lateinit var engines: EngineContainer


    @Inject
    private lateinit var mapUI: MapUI

    // 直接注入组件化后的输入处理器
    @Inject
    private lateinit var mapInputProcessor: MapInputProcessor

    @Inject
    private lateinit var mapManager: MapManager
    val multiplexer = InputMultiplexer()        // 创建多重输入处理器
    val uiStage: Stage by lazy { Stage(ScreenViewport()) }
    var selectedUnit: Entity? = null
    var unitPortrait: Image = Image()      // 先不给 drawable

    @Initiate
    fun initialize() {

    }

    fun updateUnitPortrait(entity: Entity?) {
        if (entity == null) {
            unitPortrait.drawable = null
            return
        }
        val appearance: UnitAppearanceComponent = entity.getComponent(UnitAppearanceComponent::class.java)
        unitPortrait.drawable = TextureRegionDrawable(
            TextureRegion(appearance.unitTexture)
        )
    }

    override fun show() {
//        viewport = ScreenViewport(camera)
//        gameResources=GameResources()
//        tileMap = TileMap(gameResources.camera, gameResources, terrainConfig)
        mapManager.switch("home")

        mapInputProcessor.configure(
            getSelectedUnit = { selectedUnit }
        ) { unit ->
            selectedUnit = unit
            updateUnitPortrait(unit)
        }
        multiplexer.addProcessor(uiStage)          // UI优先
        multiplexer.addProcessor(mapInputProcessor)    // 然后是游戏输入
//        multiplexer.addProcessor(gameInput)      // 最后是游戏输入
        Gdx.input.inputProcessor = multiplexer
        uiStage.isDebugAll = true
        mapUI.initializeUI(uiStage, unitPortrait, selectedUnit)
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
            texture = texture(rootElement.getChildByName("texture").text)
        }.build()
        val querySql = "SELECT id, name, health,unitTexture,mapX,mapY,unitType,tileMapId FROM things"
        gameResources.conn.createStatement().use { stmt ->
            val rs = stmt.executeQuery(querySql)
            val thingBuilder = ThingBuilder.Builder().apply {
                id = rs.getInt("id")
                name = rs.getString("name")
                health = rs.getFloat("health")
                texture = texture(rs.getString("unitTexture"))
                tileMap = mapManager.get(rs.getString("tileMapId"))?.tileMap
                mapX = rs.getInt("mapX")
                mapY = rs.getInt("mapY")
                thingUnitType = enumValueOf(rs.getString("unitType"))
                drop.add(thingBuilder1)
            }.build()
            while (rs.next()) {
                engines.createThingEntity(
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
                val unitBuilder = UnitBuilder.Builder().apply {
                    id = rs.getInt("id")
                    name = rs.getString("name")
                    age = rs.getInt("age")
                    texture = texture(rs.getString("unitTexture"))
                    tileMap = mapManager.get(rs.getString("tileMapId"))?.tileMap
                    mapX = rs.getInt("mapX")
                    mapY = rs.getInt("mapY")
                }.build()
                engines.createHumanEntity(
                    unitBuilder
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
        val renderSystem = engines.unitEngine.getSystem(RenderSystem::class.java)
        renderSystem.debugMode = true
        engines.unitEngine.update(delta)
        engines.thingEngine.update(delta)

        //在每帧渲染stage
        uiStage.act(Gdx.graphics.deltaTime.coerceAtMost(1 / 30f)) // 更新场景
        uiStage.draw()  // 绘制场景
    }

    override fun resize(width: Int, height: Int) {
        // 更新stage视图
        uiStage.viewport?.update(width, height, true)
        gameResources.viewport.update(width, height, true)
    }


//    private fun addListener(listener: io.github.lv.screen.`<anonymous>`) {}


    override fun hide() {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {
        uiStage.dispose()
    }
}
