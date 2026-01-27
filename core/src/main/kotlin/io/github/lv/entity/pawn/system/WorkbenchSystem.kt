package io.github.lv.entity.pawn.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import io.github.lv.entity.pawn.component.PathComponent
import io.github.lv.entity.pawn.component.PawnInformationComponent
import io.github.lv.entity.pawn.component.WorkComponent

class WorkbenchSystem(): MyIteratingSystem(
    Family.all(
        WorkComponent::class.java,
        PawnInformationComponent::class.java,
        PathComponent::class.java
    ).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        TODO("Not yet implemented")
    }
}
