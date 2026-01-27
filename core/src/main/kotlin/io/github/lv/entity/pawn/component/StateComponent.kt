package io.github.lv.entity.pawn.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.ai.fsm.StateMachine
import io.github.lv.entity.pawn.state.PawnState
import io.github.lv.entity.pawn.system.PawnAndThing
import ktx.ashley.Mapper

class StateComponent : Component {
    companion object: Mapper<StateComponent>()
    lateinit var pawnStateMachine: StateMachine<PawnAndThing, PawnState>
}
