package io.github.lv.ecs.pawn.system

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.ai.btree.Task.Status.*
import com.github.czyzby.autumn.annotation.Component
import io.github.lv.ecs.pawn.component.BehaviorComponent

@Component
class BehaviorSystem : MyIteratingSystem(
    Family.all(
        BehaviorComponent::class.java
    ).get()
) {
    private val behaviorMapper = ComponentMapper.getFor(BehaviorComponent::class.java)

    override fun processEntity(entity: Entity, deltaTime: Float) {
        BehaviorComponent.mapper[entity].delta = deltaTime
        BehaviorComponent.mapper[entity].tree?.step()
//        behaviorMapper[entity].tree?.step()
        val status = behaviorMapper[entity].tree?.status
        if (status != null) {
            when (status) {
                FRESH -> {}
                RUNNING -> {}
                FAILED -> {}
                SUCCEEDED -> {}
                CANCELLED -> {}
            }
        }
    }
}
