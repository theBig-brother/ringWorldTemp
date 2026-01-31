package io.github.lv.tileMap

import com.github.czyzby.autumn.annotation.Component
import io.github.lv.ecs.thing.ThingBuilder
import io.github.lv.ecs.zone.ZoneBuilder

@Component
class ConstructPlan {
    var currentThing: ThingBuilder? = null
    var currentZone: ZoneBuilder? = null
}
