package io.github.lv.ecs.pawn.system.task.work

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.utils.TimeUtils
import io.github.lv.ecs.pawn.component.BehaviorComponent
import io.github.lv.ecs.pawn.component.WorkComponent
import io.github.lv.ecs.thing.component.ThingInformationComponent

class MineTask : LeafTask<Entity>() {
    // 保存上次更新时间
    var lastTime: Long = 0
    val period = 1.0

    override fun execute(): Status {
        val currentTime = TimeUtils.nanoTime()
        if (lastTime == 0L) {
            lastTime = currentTime // 第一次调用时，初始化 lastTime
        }
        val elapsedTime = (currentTime - lastTime) / 1_000_000_000.0  // 将纳秒转为秒
        //这里明显有优化问题
        val pawn = getObject()
        val workComponent = WorkComponent.mapper[pawn]
        val behaviorComponent = BehaviorComponent.Companion.mapper[pawn]
        if (workComponent.workTarget != null) {
            val thingInformationComponent =
                workComponent.workTarget!!.getComponent(ThingInformationComponent::class.java)
            if (elapsedTime >= period) {
                lastTime = currentTime  // 更新 lastTime
                thingInformationComponent.health -=  100f
            }
            return if (thingInformationComponent.health >= 0) {
                Status.RUNNING
            } else {
                workComponent.workTarget=null
                Status.SUCCEEDED
            }
        } else {
            return Status.FAILED
        }
    }
    override fun copyTo(task: Task<Entity>) = task
}
