package io.github.lv.gameUnit

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import io.github.lv.Constant
import io.github.lv.RingWorldGame

abstract class GameUnit {
    abstract val game: RingWorldGame
    abstract val unitTexture: Texture
    abstract val unitSprite: Sprite

    // draw map
    open fun draw() {}

}
