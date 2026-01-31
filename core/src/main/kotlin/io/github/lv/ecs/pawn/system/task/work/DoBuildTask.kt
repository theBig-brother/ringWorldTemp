package io.github.lv.ecs.pawn.system.task.work

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task

class DoBuildTask : LeafTask<Entity>() {
    override fun execute(): Status {
        getObject()
        return Status.RUNNING
    }

    override fun copyTo(task: Task<Entity>) = task
}
