package io.github.lv.entity.pawn.system.walk

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.ai.steer.behaviors.Arrive
import com.badlogic.gdx.ai.steer.behaviors.Seek
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.github.czyzby.autumn.annotation.Component
import io.github.lv.Constant
import io.github.lv.entity.pawn.component.PathComponent
import io.github.lv.entity.pawn.component.PawnAppearanceComponent
import io.github.lv.entity.pawn.component.PawnMotionComponent
import io.github.lv.entity.pawn.system.MyIteratingSystem
import io.github.lv.extension.getCenter
import io.github.lv.extension.setCenterPosition
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
@Component
class MovementSystem() : MyIteratingSystem(
    Family.all(
        PawnAppearanceComponent::class.java,
        PathComponent::class.java,

        ).get()
) {

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val appearance = entity.getComponent(PawnAppearanceComponent::class.java)
        val pawnMotionComponent = entity.getComponent(PawnMotionComponent::class.java)
        val pathComponent = entity.getComponent(PathComponent::class.java)
        /* 处理单位的路径移动
        * if (movementComponent.pathIndex == 0) movementComponent.pathIndex = 1不加这句有bug
        * 走完清空路径防止污染
        * */
        if (pathComponent != null) {
            if (pathComponent.currentPath.count <= 0) return
            if (pathComponent.pathIndex == 0) pathComponent.pathIndex = 1
            if (pathComponent.pathIndex >= pathComponent.currentPath.count) {
                pathComponent.currentPath.clear()
                pathComponent.pathIndex = 0
                return
            }
            val targetNode = pathComponent.currentPath.get(pathComponent.pathIndex)
//            val arrived = moveToTarget(appearance, targetNode, deltaTime)

            // 使用Arrive行为来控制单位的平滑到达目标
            val arrived = moveToTargetWithArrive(appearance, pawnMotionComponent, targetNode, deltaTime)
            if (arrived) {
                pathComponent.pathIndex++
                pawnMotionComponent.linearVelocity.setZero()
            }
        }
    }

    override fun update(deltaTime: Float) {
        // 获取所有包含PositionComponent和MovementComponent的实体
        super.update(deltaTime)
    }

    fun moveToTargetWithArrive(
        pawnAppearanceComponent: PawnAppearanceComponent,
        pawnMotionComponent: PawnMotionComponent,
        targetNode: TileNode,
        delta: Float,
        arriveEps: Float = 0.1f
    ): Boolean {
        if (pawnAppearanceComponent.currentMap == null) {
            return false
        }
        // 计算目标位置
        val targetPosition =
            pawnAppearanceComponent.currentMap!!.mapToWorld(targetNode.mapX, targetNode.mapY, center = true)

        // 使用gdx-ai的Arrive行为
        val targetVector = Vector2(targetPosition.first, targetPosition.second)
        val target = TargetLocation(targetVector)
        val agent = MyAgent(pawnAppearanceComponent, pawnMotionComponent)

        // 设置Arrive行为
        val arriveBehavior = Arrive(agent, target).apply {
            arrivalTolerance = arriveEps * Constant.TILE_PX        // 接近多少距离认为到达 设置到达目标的容忍度
            decelerationRadius = 0.2f * Constant.TILE_PX      // 减速半径
            timeToTarget = 0.1f          // 平滑衰减时间 设置到达目标的时间
        }
        val steeringAcc = SteeringAcceleration(Vector2())
        // 计算转向并获取新的线性速度
        val steering = arriveBehavior.calculateSteering(steeringAcc)
// Update position and linear velocity. Velocity is trimmed to maximum speed
        //通过返回的steering输出控制单位的移动
        val seek = Seek(agent, target)
        val steering2 = seek.calculateSteering(steeringAcc)
//        pawnMotionComponent.linearVelocity.mulAdd(steering2.linear, delta).limit(agent.getMaxLinearSpeed());
        pawnMotionComponent.linearVelocity.set(steering.linear)  // 1. 复制steering.linear到velocity
            .nor()                     // 2. 归一化（变为单位向量）
            .scl(agent.getMaxLinearSpeed());      // 3. 缩放到最大速度
        val linearVelocity = pawnMotionComponent.linearVelocity

        pawnAppearanceComponent.unitSprite.translateX(linearVelocity.x * delta)
        pawnAppearanceComponent.unitSprite.translateY(linearVelocity.y * delta)

        //TODO转向

        // 判断是否到达目标
        val dist: Float = targetVector.dst(pawnAppearanceComponent.unitSprite.getCenter())
        return dist < arriveEps * Constant.TILE_PX
    }


//TODO 用这个代替import com.badlogic.gdx.ai.steer.behaviors.Arrive
    /**
     * @param pawnAppearanceComponent pawn的显示组件
     * @param targetNode 目标格子
     * @param delta 每帧间隔
     * @param speed 速度
     * @param arriveEps 距离多近算走到，防止抖动
     * @return 是否走到了目标格子
     *
     * */
    fun moveToTarget(
        pawnAppearanceComponent: PawnAppearanceComponent?,
        targetNode: TileNode,
        delta: Float,
        speed: Float = 1f,
        arriveEps: Float = 0.1f
    ): Boolean {

        if (pawnAppearanceComponent == null) {
            return false
        }
        val actualSpeed = speed * Constant.TILE_PX  // ✅ 创建局部变量
        val actualarriveEps = arriveEps * Constant.TILE_PX  // ✅ 创建局部变量
        val targetPosition =
            pawnAppearanceComponent.currentMap!!.mapToWorld(targetNode.mapX, targetNode.mapY, center = true)
        val dist: Float =
            Vector2(targetPosition.first, targetPosition.second).dst(pawnAppearanceComponent.unitSprite.getCenter())
        if (dist < actualarriveEps) {
            pawnAppearanceComponent.unitSprite.setCenterPosition(targetPosition.first, targetPosition.second)
            return true
        }
        val position = pawnAppearanceComponent.unitSprite.getCenter()
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
            pawnAppearanceComponent.unitSprite.setCenterPosition(targetPosition.first, targetPosition.second)
            return true
        }
        pawnAppearanceComponent.unitSprite.translateX(stepX)
        pawnAppearanceComponent.unitSprite.translateY(stepY)
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
