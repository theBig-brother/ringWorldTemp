package io.github.lv.ecs.pawn.system.task.work

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import io.github.lv.ecs.components.PositionComponent
import io.github.lv.ecs.pawn.component.PackageComponent
import io.github.lv.ecs.pawn.component.WorkComponent
import io.github.lv.ecs.thing.component.ThingAppearanceComponent

class PickUpTask : LeafTask<Entity>() {
    override fun execute(): Status {
        val pawn = getObject()
        val packageComponent = PackageComponent.mapper[pawn]
        val workComponent = WorkComponent.mapper[pawn]
        val target = workComponent.workTarget
//TODO:Pick up and haul
        val thingAppearanceComponent = ThingAppearanceComponent.mapper[target]
        thingAppearanceComponent.show = false
        if (target != null) {
            packageComponent.pack.add(target)
            return Status.SUCCEEDED
        } else {
            return Status.FAILED
        }
    }

    override fun copyTo(task: Task<Entity>) = task
}
