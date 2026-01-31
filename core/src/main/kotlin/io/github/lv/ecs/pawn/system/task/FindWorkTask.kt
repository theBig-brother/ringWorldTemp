package io.github.lv.ecs.pawn.system.task

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import io.github.lv.ecs.pawn.component.WorkComponent
import io.github.lv.ecs.thing.component.ThingInformationComponent
import io.github.lv.pawn.WorkType
import io.github.lv.pawn.WorkType.Hunt
import io.github.lv.pawn.WorkType.PlantCut
import io.github.lv.ecs.pawn.state.OrderState
import io.github.lv.ecs.pawn.state.OrderState.*

class FindWorkTask(val thingEngine: Engine) : LeafTask<Entity>() {
    override fun execute(): Status {
        val pawn = getObject()
        val workComponent = pawn.getComponent(WorkComponent::class.java)
        if (workComponent.workTarget == null) {
            //寻找下一个工作对象
            val find = findTarget(pawn)
            if (find) {
                return Status.SUCCEEDED
            } else {
                workComponent.workType = null
            }
        }
        return Status.FAILED
    }

    fun findTarget(pawnEntity: Entity): Boolean {
        val workComponent = pawnEntity.getComponent(WorkComponent::class.java)
        val entities = thingEngine.getEntitiesFor(
            Family.all(
                ThingInformationComponent::class.java
            ).get()
        )
        outer@ for (workPriority in workComponent.sortedWorkPriorities) {
            inner@ for (entity in entities) {
                val thingInformationComponent = entity.getComponent(ThingInformationComponent::class.java)
                if (thingInformationComponent.isPending
                ) {
                    if (equalWork(thingInformationComponent.workType, workPriority.workType)) {
                        //TODO（根据距离判断）
                        workComponent.workTarget = entity
                        workComponent.workType = workPriority.workType
                        return true
                    }
                }
            }
        }
        return false
    }

    override fun copyTo(task: Task<Entity>) = task
}

/**
 * @param orderState 命令类型
 * @param workType 优先级类型
 * @return 是否匹配
 */
fun equalWork(orderState: OrderState, workType: WorkType): Boolean {
    when (orderState) {
        Deconstruct -> {}
        Mine -> {
            return workType == WorkType.Mine
        }

        Haul -> {
            return workType == WorkType.Haul
        }

        Cut -> {
            return workType == PlantCut
        }

        Harvest -> {
            return workType == PlantCut
        }

        Chop -> {
            return workType == PlantCut
        }

        OrderState.Hunt -> {
            return workType == Hunt
        }

        Slaughter -> {}
        Tame -> {}
        Uninstall -> {}
        Claim -> {}
        Strip -> {}
        Open -> {}
        Plan -> {}
        RemovePlan -> {}
        UNKNOWN -> {}
    }

    return false
}

// 目标扫描方法
//    fun scanAndSetTarget(map: Array<Array<TileNode>>) {
//        var closestDistance = Float.MAX_VALUE
//        var closestTile: TileNode? = null
//
//        // 扫描地图，选择距离当前Pawn最近的目标格子
//        for (row in map) {
//            for (tile in row) {
//                if (tile.isAvailableForWork()) {
//                    val (worldX,worldY)=
//                    val distance = position.dst(tile.x.toFloat(), tile.y.toFloat())
//                    if (distance < closestDistance) {
//                        closestDistance = distance
//                        closestTile = tile
//                    }
//                }
//            }
//        }
//
//        // 设置目标
//        targetPosition = closestTile?.let { Vector2(it.x.toFloat(), it.y.toFloat()) }
//    }
