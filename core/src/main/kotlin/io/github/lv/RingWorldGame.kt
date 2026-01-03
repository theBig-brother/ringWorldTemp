package io.github.lv

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FitViewport


class RingWorldGame : Game() {
    val batch by lazy { SpriteBatch() }
    val viewport by lazy { FitViewport(Constant.ViewportWidth, Constant.ViewportHeight) }
    val font by lazy { BitmapFont() }
    override fun create() {
//  ./gradlew :lwjgl3:run --stacktrace --info
//./gradlew :lwjgl3:run --stacktrace --debug

// use libGDX's default font,font has 15pt, but we need to scale it to our viewport by ratio of viewport height to screen height
        font.setUseIntegerPositions(false)
        font.getData()?.setScale(viewport!!.getWorldHeight() / Gdx.graphics.getHeight())
//       this.setScreen(MainMenuScreen(this))
       this.setScreen(GameScreen(this))
    }

    override fun render() {
        super.render() // important!
    }
    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
//        viewport!!.update(width, height, true)
    }
    override fun dispose() {
        batch.dispose()
        font.dispose()
    }
}
