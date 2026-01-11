package io.github.lv.entity

import com.github.czyzby.autumn.annotation.Component
import com.github.czyzby.autumn.annotation.Initiate
import com.github.czyzby.autumn.annotation.Inject
import io.github.lv.entity.gameUnit.system.DestroySystem
import io.github.lv.entity.gameUnit.system.MovementSystem
import io.github.lv.entity.gameUnit.system.RenderSystem
import io.github.lv.entity.thing.system.DestroyAndDropSystem
import io.github.lv.entity.thing.system.ThingRenderSystem

@Component
class GameEngine() {
    @Inject
    private lateinit var engines: EngineContainer
    // 系统实例
    @Inject
    private lateinit var movementSystem: MovementSystem

    @Inject
    private lateinit var renderSystem: RenderSystem

    @Inject
    private lateinit var thingRenderSystem: ThingRenderSystem

    @Inject
    private lateinit var destroySystem: DestroySystem

    @Inject
    private lateinit var destroyAndDropSystem: DestroyAndDropSystem

    @Initiate
    fun initialize() {
        // 将系统添加到引擎中
        engines.unitEngine.addSystem(movementSystem)
        engines.unitEngine.addSystem(renderSystem)
        engines.unitEngine.addSystem(destroySystem)
//thingEngine
        engines.thingEngine.addSystem(thingRenderSystem)
        engines.thingEngine.addSystem(destroyAndDropSystem)
    }

    // 更新引擎
    fun update(deltaTime: Float) {
        engines.unitEngine.update(deltaTime)
        engines.thingEngine.update(deltaTime)
    }
}
