package io.github.lv.tileMap.inputProcessor

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.Vector2
import com.github.czyzby.autumn.annotation.Component
import com.github.czyzby.autumn.annotation.Inject
import io.github.lv.GameResources
import io.github.lv.ecs.EngineContainer
import io.github.lv.ecs.pawn.component.PathComponent
import io.github.lv.ecs.pawn.component.PawnAppearanceComponent
import io.github.lv.tileMap.MapManager
import io.github.lv.tileMap.TileMap
import io.github.lv.ui.MapUI

@Component
class PawnInputProcessor() : InputProcessor {
    @Inject
    private lateinit var gameResources: GameResources

    @Inject
    private lateinit var mouseStateMachine: MouseStateMachine

    @Inject
    private lateinit var engines: EngineContainer      // 或者直接 Inject Engine，看你怎么封装
    private val unitEngine get() = engines.pawnEngine  // 和原来的参数一致

    @Inject
    private lateinit var mapUI: MapUI

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

    val touchCoords = Vector2()//防止频繁创建对象

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {

        touchCoords.set(screenX.toFloat(), screenY.toFloat())
        // 获取鼠标在世界坐标中的位置
        gameResources.viewport.unproject(touchCoords)
        val worldX = touchCoords.x
        val worldY = touchCoords.y
//        if (mouseStateMachine.mouseStateMachine.currentState == MouseState.DEFAULT && button == Input.Buttons.LEFT) {
        if (button == Input.Buttons.LEFT) {
            val entities = unitEngine.getEntitiesFor(
                Family.all(PawnAppearanceComponent::class.java, PathComponent::class.java).get()
            )
            val clicked = entities.firstOrNull { entity ->
                val appearance: PawnAppearanceComponent =
                    entity.getComponent(PawnAppearanceComponent::class.java)
                val unitSprite = appearance.unitSprite
                unitSprite.boundingRectangle.contains(worldX, worldY)
            }
            onSelect(clicked)
            mapUI.rightPanelShow(true)
        }
        return false
    }

    // 其他 InputProcessor 方法的实现（不做处理，返回 false）
    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false
    override fun scrolled(amountX: Float, amountY: Float): Boolean = false
    override fun keyDown(keycode: Int): Boolean = false
    override fun keyUp(keycode: Int): Boolean = false
    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = false
    override fun mouseMoved(screenX: Int, screenY: Int): Boolean = false
    override fun keyTyped(character: Char): Boolean = false
    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false
}
