package io.github.lv.entity

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import com.badlogic.gdx.graphics.g2d.Sprite
import com.github.czyzby.autumn.annotation.Component
import io.github.lv.entity.pawn.PawnBuilder
import io.github.lv.entity.pawn.component.*
import io.github.lv.entity.pawn.state.PawnState
import io.github.lv.entity.pawn.system.PawnAndThing
import io.github.lv.entity.thing.ThingBuilder
import io.github.lv.entity.thing.component.ThingAppearanceComponent
import io.github.lv.entity.thing.component.ThingInformationComponent
import io.github.lv.extension.setCenterPosition
import io.github.lv.ui.somethingElse.GameAssets.texture
import ktx.ashley.entity
import ktx.ashley.with

@Component
class EngineContainer() {
    val pawnEngine = Engine()
    val thingEngine = Engine()
    val worldUnitEngine = Engine()
    val zoneEngine = Engine()

    // 创建一个单位实体
    // 创建实体并添加到引擎
    fun createPawnEntity(
        pawnBuilder: PawnBuilder
    ) {
        pawnEngine.entity {
            with<PathComponent>() // 添加 PathComponent 组件到实体
            with<PackageComponent>() // 添加 PathComponent 组件到实体
            with<WorkComponent>() // 添加 PathComponent 组件到实体
            with<PawnMotionComponent>() // 添加 PathComponent 组件到实体
            with<BehaviorComponent>() // 添加 PathComponent 组件到实体
            with<PawnInformationComponent> { // 添加 PawnInformationComponent 组件并初始化属性
                // 扩展方法：简化组件的添加
                id = pawnBuilder.id
                name = pawnBuilder.name
                age = pawnBuilder.age
            }
            with<StateComponent> {
                val pawnAndThing = PawnAndThing(this@entity.entity, thingEngine)
                pawnStateMachine = DefaultStateMachine(pawnAndThing)
                //初始化不算进enter
                pawnStateMachine.changeState(PawnState.IDLE)
            }
            with<PawnAppearanceComponent> {
                unitTexturePath = pawnBuilder.texturePath
                unitSprite = Sprite(texture(unitTexturePath))
                currentMap = pawnBuilder.tileMap
                if (pawnBuilder.mapX >= 0 && pawnBuilder.mapY >= 0) {
                    val temp = pawnBuilder.tileMap?.mapToWorld(pawnBuilder.mapX, pawnBuilder.mapY)
                    if (temp != null) {
                        unitSprite.setCenterPosition(temp.first, temp.second)
                    }
                }
            }
        }
    }

    // 创建物品实体并添加到引擎
    fun createThingEntity(
        thingBuilder: ThingBuilder,
    ) {
        thingEngine.entity {
            with<PositionComponent> {
                mapX = thingBuilder.mapX
                mapY = thingBuilder.mapY
            }
            with<ThingAppearanceComponent> {
                thingTexturePath = thingBuilder.texturePath
                thingTexture = texture(thingBuilder.texturePath)
                currentMap = thingBuilder.tileMap
                currentMap?.blockedMatrix[thingBuilder.mapX][thingBuilder.mapY] = true
//                currentMap?.mapMatrix[thingBuilder.mapX][thingBuilder.mapY]?.entities?.add(entity)
            }
            with<ThingInformationComponent> {
                id = thingBuilder.id
                name = thingBuilder.name
                health = thingBuilder.health
                thingUnitType = thingBuilder.thingUnitType
                drop = thingBuilder.drop
            }
        }
    }
}
