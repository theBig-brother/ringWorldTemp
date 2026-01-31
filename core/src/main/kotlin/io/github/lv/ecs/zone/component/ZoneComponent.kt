package io.github.lv.ecs.zone.component

import com.badlogic.ashley.core.Component
import io.github.lv.tileMap.TileMap
import ktx.ashley.Mapper
import ktx.collections.GdxSet

class ZoneComponent : Component {
    companion object : Mapper<ZoneComponent>()
    var id: Int=0
    var texturePath: String=""
    var tileMap: TileMap?=null
    var zoneUnitType: ZoneType=ZoneType.UNKNOWN
    var zones =GdxSet<ZoneNode>()
}
