package io.github.lv.entity.thing.component

import com.badlogic.ashley.core.Component
import io.github.lv.entity.thing.ThingBuilder
import com.badlogic.gdx.utils.Array
import io.github.lv.ui.OrderState

class ThingInformationComponent : Component {
    var id: Int = 0
    var name: String = ""
    var health: Float = 0f
    var thingUnitType : ThingUnitType =ThingUnitType.UNKNOWN
    var willDestroy=false
    var workType= OrderState.NULL
    var proress=0;//给plan用的
    var drop: Array<ThingBuilder> =Array<ThingBuilder>()
}
