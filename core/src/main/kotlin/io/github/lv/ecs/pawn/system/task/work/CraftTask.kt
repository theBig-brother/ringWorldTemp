package io.github.lv.ecs.pawn.system.task.work

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.Task.Status


class CraftTask : LeafTask<Entity>()  {
    override fun execute(): Status {
return Task.Status.SUCCEEDED
    }

    override fun copyTo(task: Task<Entity>) = task
}
