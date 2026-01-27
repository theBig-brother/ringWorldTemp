package io.github.lv.entity.pawn.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import io.github.lv.pawn.PawnWorkPriority
import io.github.lv.pawn.WorkType
import ktx.ashley.Mapper

class WorkComponent : Component {
    companion object: Mapper<WorkComponent>()
    var workPriorities: MutableList<PawnWorkPriority> = mutableListOf()
    var workType: WorkType? = null
    var workTarget: Entity? = null
    val sortedWorkPriorities: List<PawnWorkPriority>
        get() {
            // 按照优先级和 workType 顺序重排 workPriorities
            return workPriorities
                .sortedWith(compareBy({ it.priority }, { it.workType.ordinal }))
                .toList()
            // 打印排序后的结果（调试）
//            sortedWorkPriorities.forEach {
//                println("WorkType: ${it.workType}, Priority: ${it.priority}")
//            }
        }

    init {
        workPriorities = WorkType.entries.map { workType ->
            PawnWorkPriority(workType = workType, priority = 3)
        }.toMutableList()
    }
}
