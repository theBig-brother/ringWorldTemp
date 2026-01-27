package io.github.lv.entity.thing

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.Array
import io.github.lv.entity.thing.component.ThingUnitType
import io.github.lv.tileMap.TileMap

data class ThingBuilder(
    var id: Int,
    var name: String,
    var health: Float,
    var texturePath: String,
    var tileMap: TileMap?,
    var mapX: Int,
    var mapY: Int,
    var thingUnitType: ThingUnitType,
    var drop: Array<ThingBuilder>
) {
    class Builder {
        var id: Int = 0
        var name: String = ""
        var health: Float = 100f
        var texturePath: String=""
        var tileMap: TileMap? = null
        var mapX: Int = 0
        var mapY: Int = 0
        var thingUnitType: ThingUnitType = ThingUnitType.UNKNOWN
        var drop: Array<ThingBuilder> = Array<ThingBuilder>()
        fun build(): ThingBuilder {
            return ThingBuilder(id, name, health, texturePath, tileMap, mapX, mapY, thingUnitType, drop)
        }
    }
}
