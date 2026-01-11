package io.github.lv.entity

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.Sprite
import com.github.czyzby.autumn.annotation.Component
import io.github.lv.entity.gameUnit.UnitBuilder
import io.github.lv.entity.gameUnit.component.MovementComponent
import io.github.lv.entity.gameUnit.component.PackageComponent
import io.github.lv.entity.gameUnit.component.UnitAppearanceComponent
import io.github.lv.entity.gameUnit.component.UnitInformationComponent
import io.github.lv.entity.thing.ThingBuilder
import io.github.lv.entity.thing.component.PlanComponent
import io.github.lv.entity.thing.component.ThingAppearanceComponent
import io.github.lv.entity.thing.component.ThingInformationComponent
import io.github.lv.entity.thing.component.ThingUnitType
import io.github.lv.extension.setCenterPosition

@Component
class EngineContainer() {
    val unitEngine = Engine()
    val thingEngine = Engine()
    val worldUnitEngine = Engine()

    // 创建一个单位实体
    // 创建实体并添加到引擎
    fun createHumanEntity(
        unitBuilder: UnitBuilder
    ) {
        val entity = Entity()
//        entity.remove<PositionComponent?>(PositionComponent::class.java)
        // 添加MovementComponent
        val movementComponent = MovementComponent()
        entity.add(movementComponent)
        // 添加AppearanceComponent
        val unitAppearanceComponent = UnitAppearanceComponent()
        unitAppearanceComponent.unitTexture = unitBuilder.texture!!
        unitAppearanceComponent.unitSprite = Sprite(unitBuilder.texture)
        unitAppearanceComponent.currentMap = unitBuilder.tileMap
        if (unitBuilder.mapX >= 0 && unitBuilder.mapY >= 0) {
            val temp = unitBuilder.tileMap?.mapToWorld(unitBuilder.mapX, unitBuilder.mapY)
            if (temp != null) {
                unitAppearanceComponent.unitSprite.setCenterPosition(temp.first, temp.second)
            }
        }
        entity.add(unitAppearanceComponent)
        val unitInformationComponent = UnitInformationComponent()
        unitInformationComponent.id = unitBuilder.id
        unitInformationComponent.name = unitBuilder.name
        unitInformationComponent.age = unitBuilder.age
        entity.add(unitInformationComponent)
        entity.add(PackageComponent())
        // 添加实体到引擎
        unitEngine.addEntity(entity)
    }

    // 创建物品实体并添加到引擎
    fun createThingEntity(
        thingBuilder: ThingBuilder,
    ) {
        val entity = Entity()
        if (thingBuilder.thingUnitType == ThingUnitType.PLAN) {
            entity.add(planComponent(thingBuilder))
        } else {
            entity.add(thingInformationComponent(thingBuilder))
        }
        val positionComponent = PositionComponent()
        positionComponent.mapX = thingBuilder.mapX
        positionComponent.mapY = thingBuilder.mapY
        entity.add(positionComponent)
        val thingAppearanceComponent = ThingAppearanceComponent()
        thingAppearanceComponent.thingTexture = thingBuilder.texture!!
        thingAppearanceComponent.currentMap = thingBuilder.tileMap
        thingAppearanceComponent.currentMap?.blockedMatrix[thingBuilder.mapX][thingBuilder.mapY] = true
        thingAppearanceComponent.currentMap?.mapMatrix[thingBuilder.mapX][thingBuilder.mapY]?.entity = entity
        entity.add(thingAppearanceComponent)
        // 添加实体到引擎
        thingEngine.addEntity(entity)
    }
}

fun thingInformationComponent(thingBuilder: ThingBuilder): ThingInformationComponent {
    val thingInformationComponent = ThingInformationComponent()
    thingInformationComponent.id = thingBuilder.id
    thingInformationComponent.name = thingBuilder.name
    thingInformationComponent.health = thingBuilder.health
    thingInformationComponent.thingUnitType = thingBuilder.thingUnitType
    thingInformationComponent.drop = thingBuilder.drop
    return thingInformationComponent
}

fun planComponent(thingBuilder: ThingBuilder): PlanComponent {
    val planComponent = PlanComponent()
    planComponent.id = thingBuilder.id
    planComponent.name = thingBuilder.name
    return planComponent
}
