package io.github.lv.gameUnit

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.ai.pfa.Connection
import com.badlogic.gdx.ai.pfa.DefaultGraphPath
import com.badlogic.gdx.ai.pfa.GraphPath
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.math.Vector2
import io.github.lv.Constant
import io.github.lv.RingWorldGame
import com.badlogic.gdx.utils.Array
import io.github.lv.tileMap.TileNode

abstract class GameUnit {
    abstract val game: RingWorldGame
    abstract val unitTexture: Texture
    abstract val unitSprite: Sprite
    var moveTarget: GridPoint2 = GridPoint2(0, 0)
    var currentPath: GraphPath<TileNode> = DefaultGraphPath()
    var out =DefaultGraphPath<Connection<TileNode>>()
    var gridX:Int =0
    var gridY:Int =0
    var pathIndex:Int =0
    //    abstract val unitType: UnitType

    open fun draw() {}

}
