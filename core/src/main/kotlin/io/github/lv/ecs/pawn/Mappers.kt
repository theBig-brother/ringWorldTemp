package io.github.lv.ecs.pawn

import com.badlogic.ashley.core.ComponentMapper
import io.github.lv.ecs.pawn.component.PawnAppearanceComponent


object Mappers {
    val appearance: ComponentMapper<PawnAppearanceComponent?> =
        ComponentMapper.getFor<PawnAppearanceComponent?>(PawnAppearanceComponent::class.java)
//    val velocity: ComponentMapper<VelocityComponent?> = ComponentMapper.getFor<T?>(VelocityComponent::class.java)
}
