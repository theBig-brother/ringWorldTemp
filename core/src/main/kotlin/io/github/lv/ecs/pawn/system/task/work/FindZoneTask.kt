package io.github.lv.ecs.pawn.system.task.work

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import io.github.lv.ecs.pawn.component.WorkComponent
import io.github.lv.ecs.zone.component.ZoneComponent
import ktx.ashley.allOf

class FindZoneTask(val zoneEngine: Engine) : LeafTask<Entity>() {
    override fun execute(): Status {
        val pawn=`object`
        val zones = zoneEngine.getEntitiesFor(allOf(ZoneComponent::class).get())
        for (zone in zones) {
            //TODO找适合的zone
            if(zone!=null){
                val workComponent= WorkComponent.mapper.get(pawn)
                workComponent.workTarget=zone
                return Status.SUCCEEDED
            }
        }
        return Status.FAILED
    }

    override fun copyTo(task: Task<Entity>) = task
}
