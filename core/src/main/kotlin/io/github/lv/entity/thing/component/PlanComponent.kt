package io.github.lv.entity.thing.component

import com.badlogic.ashley.core.Component

class PlanComponent : Component {
    var id = 0
    var type = ThingUnitType.PLAN
    var proress=0;
    var name=""
    var description=""
}
