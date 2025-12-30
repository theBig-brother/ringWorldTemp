package io.github.lv.gameUnit

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import io.github.lv.RingWorldGame

class Shaman(override val game: RingWorldGame) : GameUnit() {
    override val unitTexture: Texture by lazy { Texture("shaman.png") }
    val demoTexture: Texture by lazy { Texture("amla-default.png") }
    override val unitSprite: Sprite = Sprite(unitTexture) // Initialize the Sprite with the bucketTexture

    //    init {
//        type = GameUnitType.SHAMAN
//    }
    override fun draw() {
        super.draw()
        unitSprite.setSize(4f, 4f); // Define the size of the sprite
        game.batch.begin()
        unitSprite.draw(game.batch); // Sprites have their own draw method
        game.batch.end()

    }
}
