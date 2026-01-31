package io.github.lv.ecs.pawn.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.ai.fsm.StateMachine
import io.github.lv.ecs.pawn.state.PawnState
import io.github.lv.ecs.pawn.system.PawnAndEngine
import ktx.ashley.Mapper

class StateComponent : Component {
    companion object: Mapper<StateComponent>()
    lateinit var pawnStateMachine: StateMachine<PawnAndEngine, PawnState>
}
