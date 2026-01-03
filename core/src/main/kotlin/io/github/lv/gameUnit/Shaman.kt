package io.github.lv.gameUnit

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import io.github.lv.Constant
import io.github.lv.RingWorldGame
import io.github.lv.extension.setCenterPosition

class Shaman(override val game: RingWorldGame) : GameUnit() {
    override val unitTexture: Texture by lazy { Texture("shaman.png") }
    override val unitSprite: Sprite = Sprite(unitTexture) // Initialize the Sprite with the bucketTexture

    //    init {
//        type = GameUnitType.SHAMAN
//    }
    override fun draw() {
        super.draw()
        val hex: Float=Constant.hexWidth
        unitSprite.setOriginCenter()
        unitSprite.setSize(hex, hex); // Define the size of the sprite
//        unitSprite.setCenterPosition(0f, 0f)
        game.batch.begin()
        unitSprite.draw(game.batch); // Sprites have their own draw method
        game.batch.end()
    }
}
