package io.github.lv.entity.thing.system

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.github.czyzby.autumn.annotation.Inject
import io.github.lv.entity.EngineContainer
import io.github.lv.entity.PositionComponent
import io.github.lv.entity.thing.component.ThingAppearanceComponent
import io.github.lv.entity.thing.component.ThingInformationComponent

@com.github.czyzby.autumn.annotation.Component
class DestroyAndDropSystem : EntitySystem() {

    @Inject
    private lateinit var engines: EngineContainer
    override fun update(deltaTime: Float) {
        val entities = engines.thingEngine.getEntitiesFor(Family.all(ThingAppearanceComponent::class.java).get())
        for (entity in entities) {
            val thingInformationComponent = entity.getComponent(ThingInformationComponent::class.java)
            val thingAppearanceComponent = entity.getComponent(ThingAppearanceComponent::class.java)
            val positionComponent = entity.getComponent(PositionComponent::class.java)
            if (thingInformationComponent.health < 0) {
//                thingAppearanceComponent.dispose()
                thingInformationComponent.isRemoved = true
                engines.thingEngine.removeEntity(entity)
                for (i in thingInformationComponent.drop) {
                    i.mapX = positionComponent.mapX
                    i.mapY = positionComponent.mapY
                    i.tileMap = thingAppearanceComponent.currentMap
                    engines.createThingEntity(i)
                }
            }
        }
    }
}
