package io.github.lv.ecs.pawn.system.work

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.github.czyzby.autumn.annotation.Component
import com.github.czyzby.autumn.annotation.Inject
import io.github.lv.ecs.EngineContainer
import io.github.lv.ecs.pawn.component.PawnAppearanceComponent
import io.github.lv.ecs.pawn.component.WorkComponent
import io.github.lv.ecs.pawn.system.MyIteratingSystem
import io.github.lv.ecs.thing.component.ThingInformationComponent
import io.github.lv.ecs.thing.component.ThingUnitType

@Component
class DestroySystem() : MyIteratingSystem( Family.all(
    PawnAppearanceComponent::class.java, WorkComponent::class.java
).get()) {
    @Inject
    private lateinit var engineContainer: EngineContainer
    // 保存上次更新时间
    var lastTime: Long = 0
    override fun processEntity(entity: Entity, deltaTime: Float) {
        TODO("Not yet implemented")
    }
    override fun update(deltaTime: Float) {
        val pawns = engineContainer.pawnEngine.getEntitiesFor(
            Family.all(
                PawnAppearanceComponent::class.java, WorkComponent::class.java
            ).get()
        )

        //这里明显有优化问题
        for (pawn in pawns) {
            val workComponent = pawn.getComponent(WorkComponent::class.java)
            if (workComponent.workTarget != null) {
                val thingInformationComponent =
                    workComponent.workTarget!!.getComponent(ThingInformationComponent::class.java)
                if (thingInformationComponent.thingUnitType == ThingUnitType.TREE) {
                    thingInformationComponent.health -= deltaTime * 100f
                }
            }
        }
    }
}
