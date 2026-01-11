package io.github.lv.entity.gameUnit.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.github.czyzby.autumn.annotation.Inject
import io.github.lv.Constant.TILE_PX
import io.github.lv.entity.EngineContainer
import io.github.lv.entity.GameEngine
import io.github.lv.extension.getCenter
import io.github.lv.extension.setCenterPosition
import io.github.lv.entity.gameUnit.component.UnitAppearanceComponent
import io.github.lv.entity.gameUnit.component.MovementComponent
import io.github.lv.tileMap.TileMap
import io.github.lv.tileMap.TileNode

/**
 * 地图坐标：
 *   (0,0) 在左上
 *   mapY 向下增大（数组行）
 *   mapX 向右增大（数组列）
 *
 * 世界坐标：
 *   (0,0) 在左下
 *   y 向上
 */
@com.github.czyzby.autumn.annotation.Component
class MovementSystem() : EntitySystem() {
    @Inject
    private lateinit var engines: EngineContainer
    override fun update(deltaTime: Float) {
        // 获取所有包含PositionComponent和MovementComponent的实体
        val entities =
            engines.unitEngine.getEntitiesFor(
                Family.all(
                    UnitAppearanceComponent::class.java,
                    MovementComponent::class.java
                ).get()
            )

        // 遍历每一个实体并处理其移动
        for (entity in entities) {
            val appearance = entity.getComponent(UnitAppearanceComponent::class.java)
            val movementComponent = entity.getComponent(MovementComponent::class.java)
            // 处理单位的路径移动
            if (movementComponent.currentPath.count <= 0) continue
            if (movementComponent.pathIndex >= movementComponent.currentPath.count) {
                movementComponent.currentPath.clear()
                movementComponent.pathIndex = 0
                continue
            }
            if (movementComponent.pathIndex == 0) movementComponent.pathIndex = 1
            val targetNode = movementComponent.currentPath.get(movementComponent.pathIndex)
            val arrived = moveToTarget(appearance, targetNode, deltaTime)
            if (arrived) {
                movementComponent.pathIndex++
            }
        }
    }

    fun moveToTarget(
        unitAppearanceComponent: UnitAppearanceComponent,
        targetNode: TileNode,
        delta: Float,
        speed: Float = 1f,
        arriveEps: Float = 0.1f
    ): Boolean {
        val actualSpeed = speed * TILE_PX  // ✅ 创建局部变量
        val actualarriveEps = arriveEps * TILE_PX  // ✅ 创建局部变量
        val targetPosition =
            unitAppearanceComponent.currentMap!!.mapToWorld(targetNode.mapX, targetNode.mapY, center = true)
        val dist: Float =
            Vector2(targetPosition.first, targetPosition.second).dst(unitAppearanceComponent.unitSprite.getCenter())
        if (dist < actualarriveEps) {
            unitAppearanceComponent.unitSprite.setCenterPosition(targetPosition.first, targetPosition.second)
            return true
        }
        val position = unitAppearanceComponent.unitSprite.getCenter()
        val dx = targetPosition.first - position.x
        val dy = targetPosition.second - position.y
        val cosθ = dx / dist
        val sinθ = dy / dist
        // 本帧最多移动 speed*delta
        val step = actualSpeed * delta
        val stepX: Float = step * cosθ
        val stepY: Float = step * sinθ
        // 防止越过目标（一步走太大）
        if (step * step >= dist) {
            unitAppearanceComponent.unitSprite.setCenterPosition(targetPosition.first, targetPosition.second)
            return true
        }
        unitAppearanceComponent.unitSprite.translateX(stepX)
        unitAppearanceComponent.unitSprite.translateY(stepY)
        return false
    }

    // 将单位放置到六边形网格上的方法
    fun placeUnitOnGrid(
        tileMap: TileMap,
        unitSprite: Sprite,
        mapX: Int,
        mapY: Int,
        byCenter: Boolean = true
    ) {
        val (x, y) = tileMap.mapToWorld(mapX, mapY, center = byCenter)//六边形格子变世界坐标
        if (byCenter) {
            unitSprite.setCenterPosition(x, y)// Set the position of the sprite
        } else {
            unitSprite.setPosition(x, y)// Set the position of the sprite
        }
    }

}
