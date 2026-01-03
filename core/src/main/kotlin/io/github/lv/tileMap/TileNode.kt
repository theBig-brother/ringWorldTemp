package io.github.lv.tileMap

import com.badlogic.gdx.graphics.Texture

data class TileNode(
    var i: Int,
    var j: Int, var id: Int
) {
    var cost = 1
    var nodeTexture: Texture? = null
}
