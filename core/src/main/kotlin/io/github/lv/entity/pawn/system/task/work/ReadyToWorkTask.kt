package io.github.lv.entity.pawn.system.task.work

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import io.github.lv.entity.pawn.component.StateComponent
import io.github.lv.entity.pawn.component.WorkComponent

class ReadyToWorkTask : LeafTask<Entity>() {
    override fun execute(): Status {
        //TODO 条件叶节点判断工作对象是不是在旁边，以及是不是对象本身已经改变了
        val pawn = getObject()
        val stateComponent = StateComponent.Companion.mapper[pawn]
        val workComponent = WorkComponent.Companion.mapper[pawn]


        return Status.SUCCEEDED
    }

    override fun copyTo(task: Task<Entity>) = task
}
