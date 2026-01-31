package io.github.lv.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.github.czyzby.autumn.annotation.Component
import com.github.czyzby.autumn.annotation.Initiate
import com.github.czyzby.autumn.annotation.Inject
import io.github.lv.Constant.TILE_PX
import io.github.lv.GameResources
import io.github.lv.tileMap.MapManager
import io.github.lv.ui.ResearchUi

@Component
class ResearchScreen : ScreenAdapter() {
    val uiStage: Stage by lazy { Stage(ScreenViewport()) }

    @Inject
    private lateinit var researchUi: ResearchUi

    @Inject
    private lateinit var mapManager: MapManager

    @Inject
    private lateinit var gameResources: GameResources

    @Initiate
    @Suppress("UNUSED")
    fun init() {

    }

    override fun render(delta: Float) {
        // 清空屏幕
        ScreenUtils.clear(Color.GRAY)
        draw(delta)
    }
    fun draw(delta: Float) {
        //在每帧渲染stage
        uiStage.act(Gdx.graphics.deltaTime.coerceAtMost(1 / 30f)) // 更新场景
        uiStage.draw()
        handleInput(delta)
    }
    override fun resize(width: Int, height: Int) {
        // 更新stage视图
        uiStage.viewport?.update(width, height, true)
//        gameResources.viewport.update(width, height, true)
    }

    override fun show() {
        Gdx.input.inputProcessor = uiStage
        uiStage.isDebugAll = true
        researchUi.initializeUI(uiStage)
        //TODO加载已经研究好的内容
    }

    override fun hide() {

    }

    override fun pause() {

    }

    override fun resume() {
    }

    override fun dispose() {
    }

    fun handleInput(delta: Float) {
        val camera = gameResources.camera
        val speedKey = 10f * TILE_PX
        val distKey = delta * speedKey
        //左移相机
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.translate(-distKey, 0f, 0f)
        }
        //右移相机
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.translate(distKey, 0f, 0f)
        }
        //下移相机
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.translate(0f, -distKey, 0f)
        }
        //上移相机
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.translate(0f, distKey, 0f)
        }
        //https://developer.aliyun.com/article/1619479
        // 鼠标中键拖拽地图
        if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {

//            val distMiddle = delta * 1f* TILE_PX不要自作聪明写这行
            val deltaX = -Gdx.input.deltaX * camera.zoom
            val deltaY = Gdx.input.deltaY * camera.zoom
            camera.translate(deltaX, deltaY, 0f)
        }
    }
}
