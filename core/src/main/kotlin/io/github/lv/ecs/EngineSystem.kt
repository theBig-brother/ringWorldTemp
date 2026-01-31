package io.github.lv.ecs

import com.github.czyzby.autumn.annotation.Component
import com.github.czyzby.autumn.annotation.Initiate
import com.github.czyzby.autumn.annotation.Inject
import io.github.lv.ecs.pawn.system.*
import io.github.lv.ecs.pawn.system.render.AnimateSystem
import io.github.lv.ecs.pawn.system.render.PawnRenderSystem
import io.github.lv.ecs.pawn.system.walk.SetPathSystem
import io.github.lv.ecs.thing.system.DestroyAndDropSystem
import io.github.lv.ecs.thing.system.ThingRenderSystem
import io.github.lv.ecs.zone.system.ZoneRenderSystem

@Component
class EngineSystem() {
    @Inject
    lateinit var engineContainer: EngineContainer

    @Inject
    private lateinit var setPathSystem: SetPathSystem

    @Inject
    private lateinit var pawnRenderSystem: PawnRenderSystem

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

    @Inject
    private lateinit var zoneRenderSystem: ZoneRenderSystem

    @Initiate
    fun initialize() {
        // 将系统添加到引擎中
        engineContainer.pawnEngine.addSystem(behaviorSystem)
        engineContainer.pawnEngine.addSystem(stateSystem)
        engineContainer.pawnEngine.addSystem(animateSystem)
        engineContainer.pawnEngine.addSystem(pawnRenderSystem)
        engineContainer.pawnEngine.addSystem(setPathSystem)
        engineContainer.thingEngine.addSystem(thingRenderSystem)
        engineContainer.thingEngine.addSystem(destroyAndDropSystem)
        engineContainer.zoneEngine.addSystem(zoneRenderSystem)
    }
}
