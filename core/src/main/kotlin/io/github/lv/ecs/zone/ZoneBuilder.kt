package io.github.lv.ecs.zone

import io.github.lv.ecs.zone.component.ZoneNode
import io.github.lv.ecs.zone.component.ZoneType
import io.github.lv.tileMap.TileMap
import ktx.collections.GdxSet
import ktx.collections.gdxSetOf

data class ZoneBuilder(
    var id: Int,
    var name:String,
    var texturePath: String,
    var tileMap: TileMap?,
    var zoneType: ZoneType,
    var zones: GdxSet<ZoneNode> = gdxSetOf()
    ) {
    class Builder {
        var id: Int = 0
        var name:String=""
        var texturePath: String = ""
        var tileMap: TileMap? = null
        var zoneType: ZoneType = ZoneType.UNKNOWN
        var zones: GdxSet<ZoneNode> = gdxSetOf()
        fun build(): ZoneBuilder {
            return ZoneBuilder(id, name,texturePath, tileMap, zoneType,zones)
        }
    }
}
