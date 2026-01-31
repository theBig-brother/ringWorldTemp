package io.github.lv.ecs.zone.system

import com.badlogic.ashley.core.EntitySystem
import com.github.czyzby.autumn.annotation.Inject
import io.github.lv.GameResources
import io.github.lv.ecs.EngineContainer
import io.github.lv.ecs.zone.component.ZoneComponent
import io.github.lv.ui.somethingElse.GameAssets.texture


import ktx.ashley.*
import ktx.graphics.use

@com.github.czyzby.autumn.annotation.Component
class ZoneRenderSystem : EntitySystem() {
    @Inject
    lateinit var engineContainer: EngineContainer

    @Inject
    lateinit var gameResources: GameResources
    override fun update(deltaTime: Float) {
        val entities = engineContainer.zoneEngine.getEntitiesFor(allOf(ZoneComponent::class).get())
        for (entity in entities) {
            val zoneComponent = ZoneComponent.mapper[entity]
            gameResources.batch.use(gameResources.camera) {
                zoneComponent.zones.forEach { zone ->
                    val pos = zoneComponent.tileMap?.mapToWorld(zone.mapX, zone.mapY)
                    val texture=texture(zoneComponent.texturePath)
                    pos?.let { it1 -> it.draw(texture, it1.first-texture.width/2, it1.second-texture.height/2) }
                }
            }
        }
    }
}
