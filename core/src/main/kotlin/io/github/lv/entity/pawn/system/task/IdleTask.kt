package io.github.lv.entity.pawn.system.task

import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.Task.Status
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.utils.TimeUtils
import io.github.lv.entity.pawn.component.StateComponent
import io.github.lv.entity.pawn.component.WorkComponent
import io.github.lv.entity.pawn.state.PawnState

class IdleTask : LeafTask<Entity>() {
    // 保存上次更新时间
    var lastTime: Long = 0
    val period = 1.0

    override fun execute(): Status {
        // TODO: 发呆 / 等待 / 播放动画/每秒寻找一次工作对象
        val currentTime = TimeUtils.nanoTime()
        if (lastTime == 0L) {
            lastTime = currentTime // 第一次调用时，初始化 lastTime
        }
        val elapsedTime = (currentTime - lastTime) / 1_000_000_000.0  // 将纳秒转为秒
        val pawn = getObject()
        val stateComponent = StateComponent.Companion.mapper[pawn]
        if (elapsedTime >= period) {
            lastTime = currentTime  // 更新 lastTime
            stateComponent.pawnStateMachine.changeState(PawnState.FindWorkState)
            return Status.SUCCEEDED
        }

        return Status.RUNNING
    }

    override fun copyTo(task: Task<Entity>) = task
}
