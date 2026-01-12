package io.github.lv.tileMap

import com.badlogic.ashley.core.Entity
import com.github.czyzby.autumn.annotation.Component
import io.github.lv.entity.thing.ThingBuilder

@Component
class WhichConstruct {
    var now: ThingBuilder? = null
}
