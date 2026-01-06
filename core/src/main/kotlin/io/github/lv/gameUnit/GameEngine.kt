package io.github.lv.gameUnit

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import io.github.lv.RingWorldGame
import io.github.lv.extension.setCenterPosition
import io.github.lv.tileMap.TileMap

class GameEngine(val game: RingWorldGame, camera: OrthographicCamera) {
    val gameEngine = Engine()

    // 系统实例
    private val movementSystem = MovementSystem(gameEngine)
    private val renderSystem = RenderSystem(game.batch, camera, gameEngine)

    init {
        // 将系统添加到引擎中
        gameEngine.addSystem(movementSystem)
        gameEngine.addSystem(renderSystem)
    }

    // 创建一个单位实体
    // 创建实体并添加到引擎
    fun createHumanEntity(
        id: Int,
        name: String,
        age: Int,
        texture: Texture,
        tilemap: TileMap,
        startX: Int = 0,
        startY: Int = 0
    ) {

        val entity = Entity()

//        entity.remove<PositionComponent?>(PositionComponent::class.java)
        // 添加MovementComponent
        val movementComponent = MovementComponent()
        entity.add(movementComponent)
        // 添加AppearanceComponent
        val appearanceComponent = AppearanceComponent()
        appearanceComponent.unitTexture = texture
        appearanceComponent.unitSprite = Sprite(texture)
        appearanceComponent.currentMap = tilemap
        if (startX >= 0 && startY >= 0) {
            val temp = tilemap.mapToWorld(startX, startY)
            appearanceComponent.unitSprite.setCenterPosition(temp.first, temp.second)
        }
        entity.add(appearanceComponent)
        val informationComponent = InformationComponent()
        informationComponent.id = id
        informationComponent.name = name
        informationComponent.age = age
        entity.add(informationComponent)
        // 添加实体到引擎
        gameEngine.addEntity(entity)
    }

    // 更新引擎
    fun update(deltaTime: Float) {
        gameEngine.update(deltaTime)
    }
}
