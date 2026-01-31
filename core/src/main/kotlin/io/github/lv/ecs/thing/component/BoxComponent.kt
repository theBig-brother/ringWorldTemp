package io.github.lv.ecs.thing.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Array
import io.github.lv.ecs.thing.ThingBuilder

class BoxComponent: Component {
    var inside: Array<ThingBuilder> =Array<ThingBuilder>()
}
