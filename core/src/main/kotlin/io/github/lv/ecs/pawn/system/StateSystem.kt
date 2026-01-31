package io.github.lv.ecs.pawn.system

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.github.czyzby.autumn.annotation.Component
import io.github.lv.ecs.pawn.component.StateComponent

@Component
class StateSystem : MyIteratingSystem(
    Family.all(
        StateComponent::class.java
    ).get()
) {
    private val stateMapper = ComponentMapper.getFor(StateComponent::class.java)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        //TODO 根据优先级切换状态
        stateMapper[entity].pawnStateMachine.update()
    }
}
