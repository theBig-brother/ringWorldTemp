package io.github.lv.gameUnit

import com.badlogic.ashley.core.ComponentMapper


object Mappers {
    val appearance: ComponentMapper<AppearanceComponent?> =
        ComponentMapper.getFor<AppearanceComponent?>(AppearanceComponent::class.java)
//    val velocity: ComponentMapper<VelocityComponent?> = ComponentMapper.getFor<T?>(VelocityComponent::class.java)
}
