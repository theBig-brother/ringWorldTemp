package io.github.lv

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import io.github.lv.extension.getCenterX
import io.github.lv.extension.getCenterY
import io.github.lv.gameUnit.Shaman
import io.github.lv.gameUnit.UnitController.getGridPosition
import io.github.lv.gameUnit.UnitController.moveToTarget
import io.github.lv.gameUnit.UnitController.updateFollowPath
import io.github.lv.gameUnit.UnitDrawDebugDecorator
import io.github.lv.tileMap.MapInputProcessor
import io.github.lv.tileMap.TileMap

class GameScreen(private val game: RingWorldGame) : ScreenAdapter() {
    var music: Music? = null
    private var skin: Skin? = null
    private var stage: Stage? = null
    val camera = OrthographicCamera(800f, 600f)  // 创建一个摄像机

    val shaman = Shaman(game)
    val viewport by lazy { FitViewport(Constant.ViewportWidth, Constant.ViewportHeight, camera) }
    private val tileMap = TileMap(game, camera)
    private val mapInputProcessor = MapInputProcessor(camera, viewport, shaman, tileMap)
    private val multiplexer = InputMultiplexer()        // 创建多重输入处理器

    // 现在使用装饰器包装:
    val decoratedShaman = UnitDrawDebugDecorator(shaman, camera)

    override fun show() {
//        stage = Stage(viewport)
//        stage?.isDebugAll = true
//        multiplexer.addProcessor(stage);          // UI优先
        multiplexer.addProcessor(mapInputProcessor);    // 然后是缩放处理器
//        multiplexer.addProcessor(gameInput);      // 最后是游戏输入
        Gdx.input.inputProcessor = multiplexer
    }

    override fun render(delta: Float) {
        input()
        logic(delta)
        draw()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
        stage?.viewport?.update(width, height, true) // 更新stage视图
    }

    fun input() {

    }

    fun logic(delta: Float) {
        getGridPosition(shaman.unitSprite.getCenterX(), shaman.unitSprite.getCenterY()).also { pos ->
            shaman.gridX = pos.first
            shaman.gridY = pos.second
        }
        try {
            updateFollowPath(shaman, delta)
        } catch (e: Exception) {
            e.printStackTrace()
        }
//        moveToTarget(shaman, shaman.moveTarget.x,shaman.moveTarget.y,delta, 3f)
    }

    fun draw() {
        // 清空屏幕
        ScreenUtils.clear(Color.BLACK)
        camera.update()
        game.batch.projectionMatrix = viewport.camera.combined
        tileMap.draw()
//        shaman.draw()
        decoratedShaman.draw()
    }

    override fun hide() {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {
        stage?.dispose()
    }
}
