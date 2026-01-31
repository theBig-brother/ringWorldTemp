package io.github.lv.ecs.pawn.system.task.work

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import io.github.lv.ecs.pawn.component.StateComponent
import io.github.lv.ecs.pawn.state.PawnState

class ToIdleTask : LeafTask<Entity>() {
    override fun execute(): Status? {
        val pawn = getObject()
        val stateComponent = StateComponent.Companion.mapper[pawn]
        stateComponent.pawnStateMachine.changeState(PawnState.IDLE)
        return Status.SUCCEEDED
    }

    override fun copyTo(task: Task<Entity>) = task
}
