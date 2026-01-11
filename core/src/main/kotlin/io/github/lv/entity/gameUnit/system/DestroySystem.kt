package io.github.lv.entity.gameUnit.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.github.czyzby.autumn.annotation.Inject
import io.github.lv.entity.EngineContainer
import io.github.lv.entity.GameEngine
import io.github.lv.entity.gameUnit.component.UnitAppearanceComponent
import io.github.lv.entity.PositionComponent
import io.github.lv.entity.thing.component.ThingInformationComponent
import io.github.lv.entity.thing.component.ThingUnitType
@com.github.czyzby.autumn.annotation.Component
class DestroySystem() : EntitySystem() {

    @Inject
    private lateinit var engines: EngineContainer
    override fun update(deltaTime: Float) {
        val units = engines.unitEngine.getEntitiesFor(
            Family.all(
                UnitAppearanceComponent::class.java
            ).get()
        )

        val things = engines.thingEngine.getEntitiesFor(
            Family.all(
                PositionComponent::class.java
            ).get()
        )
        //这里明显有优化问题
        //TODO：不要遍历整个things
        for (entity in units) {
            for (thing in things) {
                if (entity.getComponent(UnitAppearanceComponent::class.java).position == thing.getComponent(
                        PositionComponent::class.java
                    )
                ) {

                    val thingInformationComponent = thing.getComponent(ThingInformationComponent::class.java)
                    if(thingInformationComponent.thingUnitType== ThingUnitType.TREE) {
                        thingInformationComponent.health-=deltaTime*100f
                    }
                }
            }
        }
    }
}
