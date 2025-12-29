package io.github.lv

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.StretchViewport

class MainMenuScreen(private val game: RingWorldGame) : ScreenAdapter() {
    private var skin: Skin? = null
    private var stage: Stage? = null
    val backgroundTexture: Texture by lazy { Texture("images/maps/background.png") }
    val uiViewport = ScreenViewport()
    val viewport by lazy { StretchViewport(Constant.ViewportWidth, Constant.ViewportHeight) }

    override fun show() {
        stage = Stage(uiViewport)
//        stage?.isDebugAll = true
        skin = Skin(Gdx.files.internal("uiskin.json"))
        Gdx.input.inputProcessor = stage
        val table = Table()
        table.top().left() // 设置表格的位置为左上角
        table.setFillParent(true) // 让表格填满整个舞台
        // 创建Label
        val label = Label("Welcome to the Game!", skin)
        // 创建TextButton
        val button1 = TextButton("Start", skin)
        val button2 = TextButton("Options", skin)
        val button3 = TextButton("Exit", skin)
        button1.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
//                println("Button clicked!")
                game.setScreen(GameScreen(game))
                dispose()
            }
        })
        // 添加元素到表格中
        table.add(label).colspan(2).padBottom(20f) // 添加标签并合并两列
        table.row() // 换行
        table.add(button1).minWidth(150f).minHeight(50f).fillX().pad(10f) // 按钮填充水平方向并设置内边距
        table.row() // 换行
        table.add(button2).fillX().pad(10f) // 另一个按钮
        table.row() // 换行
        table.add(button3).fillX().pad(10f) // 最后一个按钮
        // 将表格添加到Stage
        stage?.addActor(table)
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(Color.BLACK)
        game.batch.setProjectionMatrix(viewport.camera.combined);
        game.batch.begin()
        game.batch.draw(
            backgroundTexture,
            0f,
            0f,
            viewport.worldWidth,
            viewport.worldHeight
        ); // draw the background
//        game.font.draw(game.batch, "Main Menu", 1f, 1f)
        game.batch.end()
//      在每帧渲染stage
        stage?.act(Gdx.graphics.deltaTime.coerceAtMost(1 / 30f)); // 更新场景
        stage?.draw();  // 绘制场景
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
        stage?.viewport?.update(width, height, true) // 更新stage视图
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun hide() {
    }

    override fun dispose() {
        stage!!.dispose()
        skin!!.dispose()
        backgroundTexture.dispose()
    }
}
