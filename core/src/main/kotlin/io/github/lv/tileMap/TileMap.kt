package io.github.lv.tileMap

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.ai.pfa.DefaultGraphPath
import com.badlogic.gdx.ai.pfa.GraphPath
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import io.github.lv.Constant
import io.github.lv.RingWorldGame
import com.badlogic.gdx.utils.Array

class TileMap(private val game: RingWorldGame, private val camera: OrthographicCamera) {
    val grassTexture: Texture by lazy { Texture("images/terrain/semi-dry.png") }
    val pinkTexture: Texture by lazy { Texture("images/terrain/reach.png") }
    val waterTexture: Texture by lazy { Texture("images/terrain/coast-tile.png") }
    val beachTexture: Texture by lazy { Texture("images/terrain/beach.png") }
    val cobblesTexture: Texture by lazy { Texture("images/terrain/cobbles-keep.png") }
    val HEIGHT = 10
    val WIDTH = 10
    val mapMatrix = Array(HEIGHT) { Array<TileNode?>(WIDTH) { null } }
    val graph = Graph(mapMatrix)
    val findPath = FindPath(graph)

    init {
        var a = 0
        for (i in 0 until mapMatrix.size) {
            for (j in 0 until mapMatrix[i].size) {
                mapMatrix[i][j] = TileNode(i, j, i * WIDTH + j)

            }
        }
        //测试用，会删除
        for (i in 0 until mapMatrix.size) {
            for (j in 0 until mapMatrix[i].size) {
                if (i % 2 == 0 && j % 2 == 0) {
                    mapMatrix[i][j]?.let { it.nodeTexture = grassTexture }
                } else if (i % 2 == 0 && j % 2 == 1) {
                    mapMatrix[i][j]?.let { it.nodeTexture = pinkTexture }
                } else if (i % 2 == 1 && j % 2 == 0) {
                    mapMatrix[i][j]?.let { it.nodeTexture = waterTexture }
                } else {
                    mapMatrix[i][j]?.let { it.nodeTexture = beachTexture }
                }
            }
        }
    }


    // draw map
    fun draw() {
        handleInput()
        game.batch.begin()
        for (i in 0 until HEIGHT) {
            for (j in 0 until WIDTH) {
                try {
                    // 可能出错的代码
                    mapMatrix[i][j]?.let {
                        game.batch.draw(
                            it.nodeTexture,
                            j * Constant.hexWidth * 0.75f,
                            i * Constant.hexHeight - (j % 2) * Constant.hexHeight / 2,
                            Constant.hexWidth,
                            Constant.hexHeight
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        game.batch.end()
    }

    fun handleInput() {
        val speedKey = 1f
        //左移相机
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.translate(-speedKey, 0f, 0f);
        }
        //右移相机
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.translate(speedKey, 0f, 0f);
        }
        //下移相机
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.translate(0f, -speedKey, 0f);
        }
        //上移相机
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.translate(0f, speedKey, 0f);
        }
        //https://developer.aliyun.com/article/1619479
        // 鼠标中键拖拽地图
        if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
            val speedMiddle = 0.05f
            val deltaX = -Gdx.input.deltaX * camera.zoom * speedMiddle
            val deltaY = Gdx.input.deltaY * camera.zoom * speedMiddle
            camera.translate(deltaX, deltaY, 0f)
        }
    }
}
