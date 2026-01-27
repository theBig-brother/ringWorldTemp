package io.github.lv.entity.pawn.system.task.work

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import io.github.lv.entity.PositionComponent
import io.github.lv.entity.pawn.component.PathComponent
import io.github.lv.entity.pawn.component.StateComponent
import io.github.lv.entity.pawn.component.TargetPosition
import io.github.lv.entity.pawn.component.WorkComponent
import io.github.lv.entity.pawn.state.PawnState
import io.github.lv.entity.thing.component.ThingInformationComponent

class WhereToWorkTask() : LeafTask<Entity>() {
    override fun execute(): Status {

        val pawn = getObject()
        val workComponent = pawn.getComponent(WorkComponent::class.java)
        val pathComponent = pawn.getComponent(PathComponent::class.java)
        val stateComponent = StateComponent.Companion.mapper[pawn]
        if (workComponent.workTarget != null) {
            //设定移动位置是workTarget的位置
            val thingInformationComponent =
                workComponent.workTarget!!.getComponent(ThingInformationComponent::class.java)
            val positionComponent =
                workComponent.workTarget!!.getComponent(PositionComponent::class.java)
            if (thingInformationComponent.isRemoved) workComponent.workTarget = null
            else {
                pathComponent.targetPosition = TargetPosition(positionComponent.mapX, positionComponent.mapY)
            }
            stateComponent.pawnStateMachine.changeState(PawnState.WORK)
            return Status.SUCCEEDED
        }
        return Status.FAILED
    }

    override fun copyTo(task: Task<Entity>) = task
}
