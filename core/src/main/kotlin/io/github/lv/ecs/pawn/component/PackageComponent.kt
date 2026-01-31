package io.github.lv.ecs.pawn.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import ktx.ashley.Mapper
import ktx.collections.gdxSetOf


class PackageComponent : Component {
    companion object : Mapper<PackageComponent>()

    var equipment = gdxSetOf<Entity>()
    var pack: MutableSet<Entity> = hashSetOf()
}
