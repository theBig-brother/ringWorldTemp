package io.github.lv.tileMap

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.github.czyzby.autumn.annotation.Component
import com.github.czyzby.autumn.annotation.Inject
import io.github.lv.GameResources
import io.github.lv.entity.EngineContainer
import io.github.lv.entity.gameUnit.component.MovementComponent
import io.github.lv.entity.gameUnit.component.UnitAppearanceComponent
import io.github.lv.ui.MapUI
import io.github.lv.ui.MouseState
import io.github.lv.ui.OrderState

@Component
class MapInputProcessor() : InputProcessor {
    // 这些是“长期依赖”，可以直接让 Autumn 管
    @Inject
    private lateinit var gameResources: GameResources

    @Inject
    private lateinit var whichConstruct: WhichConstruct

    @Inject
    private lateinit var mapUI: MapUI

    @Inject
    private lateinit var engines: EngineContainer      // 或者直接 Inject Engine，看你怎么封装
    private val unitEngine get() = engines.unitEngine  // 和原来的参数一致

    @Inject
    private lateinit var mapManager: MapManager


    // 这些是“每个地图/每次 show() 不一样”的，留成属性
    lateinit var tileMap: TileMap
        private set

    private var getSelectedUnit: () -> Entity? = { null }
    private var onSelect: (Entity?) -> Unit = {}

    /** 在 MapScreen.show() 里调用一次，把当前地图和回调喂进来 */
    fun configure(
        getSelectedUnit: () -> Entity?,
        onSelect: (Entity?) -> Unit
    ) {
        this.tileMap = mapManager.current()?.tileMap!!
        this.getSelectedUnit = getSelectedUnit
        this.onSelect = onSelect
    }

    // 创建一个摄像机
    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        // 获取鼠标在世界坐标中的位置
        val screenX = Gdx.input.x.toFloat()
        val screenY = Gdx.input.y.toFloat()
        val worldBefore = gameResources.viewport.unproject(Vector2(screenX, screenY))
        // 计算新旧缩放比例,确保 zoom 的值不会太小或太大,等于coerceIn(0.5f, 3f)
        val newZoom = MathUtils.clamp(
            gameResources.camera.zoom + amountY * 0.1f,
            0.5f,
            3.0f
        )
        gameResources.camera.zoom = newZoom
        // 重新计算鼠标位置在缩放后的世界坐标
        // 注意：这里需要先将相机更新，然后重新计算unproject
        gameResources.camera.update()
        // 获取缩放后的相同屏幕位置对应的世界坐标
        val worldAfter = gameResources.viewport.unproject(Vector2(screenX, screenY))
        // 计算缩放前后鼠标世界坐标的偏移
        val dx = worldAfter.x - worldBefore.x
        val dy = worldAfter.y - worldBefore.y
        // 调整相机位置来补偿这个偏移，保持鼠标指向的世界位置不变
        gameResources.camera.position.x -= dx
        gameResources.camera.position.y -= dy
        gameResources.camera.update()
        return true
    }

    val touchCoords = Vector2()//防止频繁创建对象
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        touchCoords.set(screenX.toFloat(), screenY.toFloat())
        // 获取鼠标在世界坐标中的位置
        gameResources.viewport.unproject(touchCoords)
        val worldX = touchCoords.x
        val worldY = touchCoords.y
        // 将鼠标位置转换为格子坐标
        val (targetGridX, targetGridY) = tileMap.worldToMap(worldX, worldY)
        if (button == Input.Buttons.LEFT) {
            if (gameResources.mouseState == MouseState.ORDER) {
                mapManager.current()?.tileMap?.drawRect = true
                mapManager.current()?.tileMap?.startRect = touchCoords
            } else if (gameResources.mouseState == MouseState.CONSTRUCTION) {
                whichConstruct.now?.tileMap = mapManager.current()?.tileMap
                whichConstruct.now?.mapX = targetGridX
                whichConstruct.now?.mapY = targetGridY
                whichConstruct.now?.let { engines.createThingEntity(it) }
            }
            mapUI.clearWorldPlaceholder()
            val world = gameResources.viewport.unproject(touchCoords)
            val entities = unitEngine.getEntitiesFor(
                Family.all(UnitAppearanceComponent::class.java, MovementComponent::class.java).get()
            )
            val clicked = entities.firstOrNull { entity ->
                val appearance: UnitAppearanceComponent =
                    entity.getComponent(UnitAppearanceComponent::class.java)
                val unitSprite = appearance.unitSprite
                unitSprite.boundingRectangle.contains(world.x, world.y)
            }
            onSelect(clicked)
            return true
        }
        if (button == Input.Buttons.RIGHT) { // 右键
            gameResources.mouseState = MouseState.DEFAULT
            gameResources.orderState = OrderState.NULL
            mapManager.current()?.tileMap?.drawRect = false

            println("rightClick: ($targetGridX, $targetGridY) ")
//            debugViewportInfo()
// 对 unit 寻路/下命令
            val entity = getSelectedUnit() ?: return true
            val movementComponent: MovementComponent = entity.getComponent(MovementComponent::class.java)
            if (tileMap.inBounds(targetGridY, targetGridX)) {
//                if (movementComponent.behavior == Behavior.DRAFT) {
                // 单位寻路
                val position = entity.getComponent(UnitAppearanceComponent::class.java).position
                tileMap.findPath?.let {
                    if (it.findPathNode(
                            tileMap.mapMatrix[position.mapY][position.mapX],
                            tileMap.mapMatrix[targetGridY][targetGridX],
                            movementComponent.currentPath
                        )
                    ) {
                        movementComponent.pathIndex = 0
                        for (i in movementComponent.currentPath) {
                            print("Grid(:${i.mapX},${i.mapY}) ")
                        }
                        println()
                    }
                }
            }
            return true
        }
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button == Input.Buttons.LEFT) {
            mapManager.current()?.tileMap?.drawRect = false
            return true
        }
        return false
    }

    fun debugViewportInfo() {
        println("=== 视口调试信息 ===")
        println("屏幕尺寸: ${Gdx.graphics.width} x ${Gdx.graphics.height}")
        println("视口类型: ${gameResources.viewport.javaClass.simpleName}")
        println("相机位置: ${gameResources.camera.position}")
        println("相机缩放: ${gameResources.camera.zoom}")
        println("视口大小: ${gameResources.viewport.worldWidth} x ${gameResources.viewport.worldHeight}")
        println("是否Y向上: ${gameResources.camera.up.y > 0}")  // 应为true（默认1,0,0）

        // 测试四个角的转换
        val corners = arrayOf(
            Vector2(0f, 0f),  // 左下
            Vector2(Gdx.graphics.width.toFloat(), 0f),  // 右下
            Vector2(0f, Gdx.graphics.height.toFloat()),  // 左上
            Vector2(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())  // 右上
        )

        corners.forEach { screenPos ->
            val worldPos = gameResources.viewport.unproject(Vector2(screenPos))
            println("屏幕$screenPos -> 世界$worldPos")
        }
    }

    // 其他 InputProcessor 方法的实现（不做处理，返回 false）
    override fun keyDown(keycode: Int): Boolean = false
    override fun keyUp(keycode: Int): Boolean = false

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = false
    override fun mouseMoved(screenX: Int, screenY: Int): Boolean = false
    override fun keyTyped(character: Char): Boolean = false
    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false
}
