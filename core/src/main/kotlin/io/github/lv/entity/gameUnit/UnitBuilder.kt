package io.github.lv.entity.gameUnit

import com.badlogic.gdx.graphics.Texture
import io.github.lv.tileMap.TileMap

data class UnitBuilder(
    var id: Int,
    var name: String,
    var age: Int,
    var texture: Texture?,
    var tileMap: TileMap?,
    var mapX: Int,
    var mapY: Int
) {
    class Builder {
        var id: Int = 0
        var name: String = ""
        var age: Int = 100
        var texture: Texture? = null
        var tileMap: TileMap? = null
        var mapX: Int = 0
        var mapY: Int = 0

        fun build(): UnitBuilder {
            return UnitBuilder(id, name, age, texture, tileMap, mapX, mapY)
        }
    }
}
