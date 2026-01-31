package io.github.lv.ecs.pawn.system.task.work

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.Task.Status
import io.github.lv.ecs.components.PositionComponent
import io.github.lv.ecs.pawn.component.PackageComponent
import io.github.lv.ecs.pawn.component.WorkComponent

class PutDownTask : LeafTask<Entity>() {
    override fun execute(): Status {
        val pawn = getObject()
        val packageComponent = PackageComponent.mapper[pawn]
        val workComponent = WorkComponent.mapper[pawn]
        val target = workComponent.workTarget
        val thingPositionComponent = PositionComponent.mapper[target]

        return Status.SUCCEEDED
    }

    override fun copyTo(task: Task<Entity>) = task
}
