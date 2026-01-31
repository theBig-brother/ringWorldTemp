package io.github.lv.tileMap.inputProcessor

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.Vector2
import com.github.czyzby.autumn.annotation.Component
import com.github.czyzby.autumn.annotation.Inject
import io.github.lv.GameResources
import io.github.lv.ecs.EngineContainer
import io.github.lv.ecs.pawn.component.PathComponent
import io.github.lv.ecs.pawn.component.TargetPosition
import io.github.lv.tileMap.MapManager
import io.github.lv.tileMap.TileMap
import io.github.lv.tileMap.ConstructPlan
import io.github.lv.ui.MapUI
import io.github.lv.ecs.pawn.state.OrderState
import io.github.lv.ecs.thing.component.ThingInformationComponent

@Component
class OrderInputProcessor() : InputProcessor {
    // 这些是“长期依赖”，可以直接让 Autumn 管
    @Inject
    private lateinit var gameResources: GameResources

    @Inject
    private lateinit var mouseStateMachine: MouseStateMachine

    @Inject
    private lateinit var constructPlan: ConstructPlan

    @Inject
    private lateinit var mapUI: MapUI

    @Inject
    private lateinit var engineContainer: EngineContainer      // 或者直接 Inject Engine，看你怎么封装
    private val unitEngine get() = engineContainer.pawnEngine  // 和原来的参数一致

    @Inject
    private lateinit var mapManager: MapManager

    // 这些是“每个地图/每次 show() 不一样”的，留成属性
    lateinit var tileMap: TileMap
        private set

    private var getSelectedUnit: () -> Entity? = { null }

    /** 在 MapScreen.show() 里调用一次，把当前地图和回调喂进来 */
    fun configure(
        getSelectedUnit: () -> Entity?
    ) {
        this.tileMap = mapManager.current()?.tileMap!!
        this.getSelectedUnit = getSelectedUnit
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return when (mouseStateMachine.mouseStateMachine.currentState) {
            MouseState.ZONE -> {
                if (amountY < 0) {
                    mouseStateMachine.radius++
                } else {
                    if (mouseStateMachine.radius >0) {
                        mouseStateMachine.radius--
                    }
                }
                true
            }

            else -> {
                false
            }
        }
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
            mouseStateMachine.startRect = touchCoords
            when (mouseStateMachine.mouseStateMachine.currentState) {
                MouseState.DEFAULT -> {
                    mapUI.clearWorldPlaceholder()
                    mapUI.rightPanelShow(false)
                }

                MouseState.ORDER_RECT -> {}
                MouseState.CONSTRUCTION -> {
                    constructPlan.currentThing?.tileMap = mapManager.current()?.tileMap
                    constructPlan.currentThing?.mapX = targetGridX
                    constructPlan.currentThing?.mapY = targetGridY
                    constructPlan.currentThing?.let { engineContainer.createThingEntity(it) }
                }

                MouseState.WALL -> {}
                MouseState.ZONE -> {

                }

                MouseState.FLOOR -> {

                }
            }
            return true
        }

        if (button == Input.Buttons.RIGHT) { // 右键
            mouseStateMachine.mouseStateMachine.changeState(MouseState.DEFAULT)
            gameResources.orderState = OrderState.UNKNOWN
            println("rightClick: ($targetGridX, $targetGridY) ")
//            debugViewportInfo()
// 对 unit 寻路/下命令
            val entity = getSelectedUnit() ?: return true
            val pathComponent: PathComponent = entity.getComponent(PathComponent::class.java)
            pathComponent.targetPosition = TargetPosition(targetGridX, targetGridY)
            return true
        }
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button == Input.Buttons.LEFT) {
            when (mouseStateMachine.mouseStateMachine.currentState) {
                MouseState.DEFAULT -> {}
                MouseState.ORDER_RECT -> {
                    mouseStateMachine.waitForPending.forEach {
                        entity->
                        val thingInformationComponent = entity.getComponent(ThingInformationComponent::class.java)
                        thingInformationComponent.isPending=true
                    }
                }
                MouseState.CONSTRUCTION -> {

                }
                MouseState.WALL -> {}
                MouseState.ZONE -> {
                    constructPlan.currentZone?.let { engineContainer.createZoneEntity(it) }
                    constructPlan.currentZone?.tileMap?.entityId++//好像没用
                    constructPlan.currentZone?.zones?.clear()
                }

                MouseState.FLOOR -> {}
            }
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
