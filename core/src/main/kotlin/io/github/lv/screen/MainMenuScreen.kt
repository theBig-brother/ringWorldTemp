package io.github.lv.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.github.czyzby.autumn.annotation.Inject
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.VisUI.SkinScale
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import io.github.lv.GameResources
import io.github.lv.RingWorldGame


class MainMenuScreen(private val game: RingWorldGame, val font: BitmapFont) : ScreenAdapter() {
    private var skin: Skin? = null
    private var stage: Stage? = null
    val backgroundTexture: Texture by lazy { Texture("images/maps/background.png") }
    val uiViewport = ScreenViewport()

    //val viewport by lazy { FillViewport(Constant.ViewportWidth, Constant.ViewportHeight) }
    val viewport by lazy { ScreenViewport() }
    @Inject
    private lateinit var gameScreen: GameScreen
    @Inject
    private lateinit var resources: GameResources
    override fun show() {
        stage = Stage(uiViewport)
//      stage?.isDebugAll = true
        skin = Skin(Gdx.files.internal("uiskin.json"))
        visui()
        Gdx.input.inputProcessor = stage

    }

    fun visui() {
        VisUI.load(SkinScale.X1)
        val root = VisTable()
        root.top().left() // 设置表格的位置为左上角
        root.setFillParent(true) // 让表格填满整个舞台
        // 创建Label
        val label = VisLabel("Welcome to the Game!")
        // 创建TextButton
        val button1 = VisTextButton("Start")
        val button2 = VisTextButton("Options")
        val button3 = VisTextButton("Exit")
        button1.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
//                println("Button clicked!")
                game.setScreen(gameScreen)
                dispose()
            }
        })
        // 添加元素到表格中
        root.add(label).colspan(2).padBottom(20f) // 添加标签并合并两列
        root.row() // 换行
        root.add(button1).minWidth(150f).minHeight(50f).fillX().pad(10f) // 按钮填充水平方向并设置内边距
        root.row() // 换行
        root.add(button2).fillX().pad(10f) // 另一个按钮
        root.row() // 换行
        root.add(button3).fillX().pad(10f) // 最后一个按钮
        stage!!.addActor(root)
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(Color.BLACK)
        resources.batch.setProjectionMatrix(viewport.camera.combined);
        resources.batch.begin()
        resources.batch.draw(
            backgroundTexture,
            0f,
            0f,
            viewport.worldWidth,
            viewport.worldHeight
        ); // draw the background
//        game.font.draw(game.batch, "Main Menu", 1f, 1f)
        resources.batch.end()
//      在每帧渲染stage
        stage?.act(Gdx.graphics.deltaTime.coerceAtMost(1 / 30f)); // 更新场景
//        stage.apply { viewport.camera.update() }
        stage?.draw();  // 绘制场景
    }

    override fun resize(width: Int, height: Int) {
        stage?.viewport?.update(width, height, true) // 更新stage视图
        viewport.update(width, height, true)
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
