package io.github.lv.entity.gameUnit

import com.badlogic.ashley.core.ComponentMapper
import io.github.lv.entity.gameUnit.component.UnitAppearanceComponent


object Mappers {
    val appearance: ComponentMapper<UnitAppearanceComponent?> =
        ComponentMapper.getFor<UnitAppearanceComponent?>(UnitAppearanceComponent::class.java)
//    val velocity: ComponentMapper<VelocityComponent?> = ComponentMapper.getFor<T?>(VelocityComponent::class.java)
}
