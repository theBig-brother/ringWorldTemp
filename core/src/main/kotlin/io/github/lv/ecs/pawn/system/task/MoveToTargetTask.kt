package io.github.lv.ecs.pawn.system.task

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.ai.steer.behaviors.Arrive
import com.badlogic.gdx.math.Vector2
import io.github.lv.Constant
import io.github.lv.ecs.pawn.component.BehaviorComponent
import io.github.lv.ecs.pawn.component.PathComponent
import io.github.lv.ecs.pawn.component.PawnAppearanceComponent
import io.github.lv.ecs.pawn.component.PawnMotionComponent
import io.github.lv.ecs.pawn.system.walk.MyAgent
import io.github.lv.ecs.pawn.system.walk.TargetLocation
import io.github.lv.extension.getCenter
import io.github.lv.tileMap.TileNode
import ktx.ashley.*


class MoveToTargetTask : LeafTask<Entity>() {
    override fun execute(): Status {
        val entity = getObject()
        val appearance: PawnAppearanceComponent? = entity[PawnAppearanceComponent.Companion.mapper]
        val behaviorComponent: BehaviorComponent? = entity[BehaviorComponent.Companion.mapper]
        val pawnMotionComponent = entity.getComponent(PawnMotionComponent::class.java)
        val pathComponent = entity.getComponent(PathComponent::class.java)
        /** 处理单位的路径移动
         * if (movementComponent.pathIndex == 0) movementComponent.pathIndex = 1不加这句有bug
         * 走完清空路径防止污染
         */
        if (pathComponent != null) {
            if (pathComponent.currentPath.count <= 0) return Status.FAILED
            if (pathComponent.pathIndex == 0) pathComponent.pathIndex = 1
            if (pathComponent.pathIndex >= pathComponent.currentPath.count) {
                pathComponent.currentPath.clear()
                pathComponent.pathIndex = 0
                return Status.SUCCEEDED
            }
            val targetNode = pathComponent.currentPath.get(pathComponent.pathIndex)
//            val arrived = moveToTarget(appearance, targetNode, deltaTime)
            // 使用Arrive行为来控制单位的平滑到达目标
            val arrived = moveToTargetWithArrive(appearance, pawnMotionComponent, targetNode, behaviorComponent!!.delta)
            if (arrived) {
                pathComponent.pathIndex++
                pawnMotionComponent.linearVelocity.setZero()
            }
        }
        return Status.RUNNING
    }

    fun moveToTargetWithArrive(
        pawnAppearanceComponent: PawnAppearanceComponent?,
        pawnMotionComponent: PawnMotionComponent,
        targetNode: TileNode,
        delta: Float,
        arriveEps: Float = 0.1f
    ): Boolean {
        if (pawnAppearanceComponent == null || pawnAppearanceComponent.currentMap == null) {
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
//        val seek = Seek(agent, target)
//        val steering2 = seek.calculateSteering(steeringAcc)
//        pawnMotionComponent.linearVelocity.mulAdd(steering2.linear, delta).limit(agent.getMaxLinearSpeed());
        pawnMotionComponent.linearVelocity.set(steering.linear)  // 1. 复制steering.linear到velocity
            .nor()                     // 2. 归一化（变为单位向量）
            .scl(agent.getMaxLinearSpeed())      // 3. 缩放到最大速度
        val linearVelocity = pawnMotionComponent.linearVelocity

        pawnAppearanceComponent.unitSprite.translateX(linearVelocity.x * delta)
        pawnAppearanceComponent.unitSprite.translateY(linearVelocity.y * delta)

        //TODO转向

        // 判断是否到达目标
        val dist: Float = targetVector.dst(pawnAppearanceComponent.unitSprite.getCenter())
        return dist < arriveEps * Constant.TILE_PX
    }

    override fun copyTo(task: Task<Entity>) = task
}
