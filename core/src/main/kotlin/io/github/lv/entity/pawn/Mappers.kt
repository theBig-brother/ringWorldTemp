package io.github.lv.entity.pawn

import com.badlogic.ashley.core.ComponentMapper
import io.github.lv.entity.pawn.component.PawnAppearanceComponent


object Mappers {
    val appearance: ComponentMapper<PawnAppearanceComponent?> =
        ComponentMapper.getFor<PawnAppearanceComponent?>(PawnAppearanceComponent::class.java)
//    val velocity: ComponentMapper<VelocityComponent?> = ComponentMapper.getFor<T?>(VelocityComponent::class.java)
}
