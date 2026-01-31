package io.github.lv.tileMap.inputProcessor

import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import com.badlogic.gdx.math.Vector2
import com.github.czyzby.autumn.annotation.Component
import com.github.czyzby.autumn.annotation.Inject
import io.github.lv.GameResources
import io.github.lv.ecs.EngineContainer
import io.github.lv.tileMap.ConstructPlan
import io.github.lv.tileMap.MapManager
import ktx.collections.GdxSet
import ktx.collections.gdxSetOf
import com.badlogic.ashley.core.Entity

@Component
class MouseStateMachine {
    // 使用 MoveState 类作为初始状态
    val mouseStateMachine: DefaultStateMachine<MouseStateMachine, MouseState> =
        DefaultStateMachine(this, MouseState.DEFAULT)
    var startRect: Vector2 = Vector2()
    val touchPos = Vector2() //防止频繁创建对象
    var radius: Int = 2
    var waitForPending: GdxSet<Entity> = gdxSetOf()
    @Inject
    lateinit var constructPlan: ConstructPlan

    @Inject
    lateinit var mapManager: MapManager

    @Inject
    lateinit var gameResources: GameResources

    @Inject
    lateinit var engines: EngineContainer
    fun update() {
        mouseStateMachine.update()
    }
}
