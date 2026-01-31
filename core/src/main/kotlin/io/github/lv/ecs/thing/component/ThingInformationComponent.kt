package io.github.lv.ecs.thing.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Array
import io.github.lv.ecs.thing.ThingBuilder
import io.github.lv.ecs.pawn.state.OrderState
import io.github.lv.ecs.zone.component.ZoneComponent
import ktx.ashley.Mapper

class ThingInformationComponent : Component {
    companion object : Mapper<ZoneComponent>()

    var id: Int = 0
    var name: String = ""
    var health: Float = 0f
    var thingUnitType: ThingUnitType = ThingUnitType.UNKNOWN
    var workType: OrderState = OrderState.UNKNOWN
    var isRemoved = false
    var isPending = false
    var drop: Array<ThingBuilder> = Array<ThingBuilder>()
}
