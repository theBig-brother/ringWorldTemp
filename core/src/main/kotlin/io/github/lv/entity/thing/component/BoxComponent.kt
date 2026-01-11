package io.github.lv.entity.thing.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Array
import io.github.lv.entity.thing.ThingBuilder

class BoxComponent: Component {
    var inside: Array<ThingBuilder> =Array<ThingBuilder>()
}
