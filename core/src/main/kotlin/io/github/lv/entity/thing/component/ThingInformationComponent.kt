package io.github.lv.entity.thing.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Array
import io.github.lv.entity.thing.ThingBuilder
import io.github.lv.pawn.WorkType
import io.github.lv.entity.pawn.state.OrderState

class ThingInformationComponent : Component {
    var id: Int = 0
    var name: String = ""
    var health: Float = 0f
    var thingUnitType : ThingUnitType =ThingUnitType.UNKNOWN
    var workType: OrderState = OrderState.UNKNOWN
    var isRemoved=false
   var  isPending=false
    var drop: Array<ThingBuilder> =Array<ThingBuilder>()
}
