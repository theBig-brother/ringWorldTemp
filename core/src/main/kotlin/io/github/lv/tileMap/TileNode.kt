package io.github.lv.tileMap

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture

data class TileNode(
    var mapX : Int,
    var mapY: Int,
    var id: Int
) {
    var cost = 1
    var nodeTexture: Texture?= null
    var string:String = ""
    var entity: Entity?= null
}
