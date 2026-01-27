package io.github.lv.entity

import com.github.czyzby.autumn.annotation.Component
import com.github.czyzby.autumn.annotation.Initiate
import com.github.czyzby.autumn.annotation.Inject
import io.github.lv.entity.pawn.system.*
import io.github.lv.entity.pawn.system.render.AnimateSystem
import io.github.lv.entity.pawn.system.render.RenderSystem
import io.github.lv.entity.pawn.system.walk.SetPathSystem
import io.github.lv.entity.thing.system.DestroyAndDropSystem
import io.github.lv.entity.thing.system.ThingRenderSystem

@Component
class EngineSystem() {
    @Inject
    private lateinit var engines: EngineContainer

    @Inject
    private lateinit var setPathSystem: SetPathSystem

    @Inject
    private lateinit var renderSystem: RenderSystem

    @Inject
    private lateinit var stateSystem: StateSystem

    @Inject
    private lateinit var behaviorSystem: BehaviorSystem

    @Inject
    private lateinit var thingRenderSystem: ThingRenderSystem

    @Inject
    private lateinit var destroyAndDropSystem: DestroyAndDropSystem

    @Inject
    private lateinit var animateSystem: AnimateSystem


    @Initiate
    fun initialize() {
        // 将系统添加到引擎中
//        engines.pawnEngine.addSystem(movementSystem)
        engines.pawnEngine.addSystem(behaviorSystem)
        engines.pawnEngine.addSystem(stateSystem)
        engines.pawnEngine.addSystem(animateSystem)
        engines.pawnEngine.addSystem(renderSystem)
        engines.pawnEngine.addSystem(setPathSystem)
//thingEngine
        engines.thingEngine.addSystem(thingRenderSystem)
        engines.thingEngine.addSystem(destroyAndDropSystem)
    }

    // 更新引擎
    fun update(deltaTime: Float) {
        engines.pawnEngine.update(deltaTime)
        engines.thingEngine.update(deltaTime)
    }
}
