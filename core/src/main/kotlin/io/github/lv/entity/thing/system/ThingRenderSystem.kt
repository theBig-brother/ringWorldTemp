package io.github.lv.entity.thing.system

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.github.czyzby.autumn.annotation.Inject
import io.github.lv.GameResources
import io.github.lv.entity.EngineContainer
import io.github.lv.entity.PositionComponent
import io.github.lv.entity.thing.component.ThingAppearanceComponent
import io.github.lv.entity.thing.component.ThingInformationComponent
import io.github.lv.entity.thing.component.ThingUnitType
import io.github.lv.ui.somethingElse.GameAssets

// 渲染系统，用来渲染所有包含AppearanceComponent的实体
@com.github.czyzby.autumn.annotation.Component
class ThingRenderSystem() : EntitySystem() {
    @Inject
    private lateinit var resources: GameResources

    @Inject
    private lateinit var engines: EngineContainer
    override fun update(deltaTime: Float) {
        val entities = engines.thingEngine.getEntitiesFor(
            Family.all(
                ThingAppearanceComponent::class.java,
                ThingInformationComponent::class.java
            ).get()
        )
        val plans = engines.thingEngine.getEntitiesFor(
            Family.all(
                ThingAppearanceComponent::class.java,
                ThingInformationComponent::class.java
            ).get()
        )
        resources.batch.begin()
        // 遍历并渲染每个实体的精灵
        for (entity in entities) {
            val thingAppearanceComponent = entity.getComponent(ThingAppearanceComponent::class.java)
            val thingInformationComponent = entity.getComponent(ThingInformationComponent::class.java)
            val positionComponent = entity.getComponent(PositionComponent::class.java)
            val (worldX, worldY) = thingAppearanceComponent.currentMap!!.mapToWorld(
                positionComponent.mapX,
                positionComponent.mapY
            )
            val texture = thingAppearanceComponent.thingTexture
            resources.batch.draw(
                texture,
                worldX - texture.width.toFloat() / 2f,
                worldY - texture.height.toFloat() / 2f
            )
            if (thingInformationComponent.isPending) {
                val texture = when (thingInformationComponent.thingUnitType) {
                    ThingUnitType.TREE -> GameAssets.texture("data/core/images/items/axe-small.png")
                    ThingUnitType.BLOCK -> GameAssets.texture("data/core/images/items/hammer.png")
                    ThingUnitType.MINE -> GameAssets.texture("data/core/images/items/crossbow.png")//请假装这是十字稿

                    else -> null
                }
                if (texture != null) {
                    resources.batch.draw(
                        texture,
                        worldX - texture.width.toFloat() / 2f,
                        worldY - texture.height.toFloat() / 2f
                    )
                }

            }
        }

        resources.batch.end()
    }
}
