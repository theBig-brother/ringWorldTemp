package io.github.lv.tileMap

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import io.github.lv.Constant
import io.github.lv.RingWorldGame

class TileMap(private val game: RingWorldGame, private val camera: OrthographicCamera) {
    val grassTexture: Texture by lazy { Texture("semi-dry.png") }
    val pinkTexture: Texture by lazy { Texture("reach.png") }
    val waterTexture: Texture by lazy { Texture("coast-tile.png") }
    val beachTexture: Texture by lazy { Texture("beach.png") }
    val hex = Constant.hexWidth
    val twoDArray = Array(40) { Array(40) { 0 } }

    // draw map
    fun draw() {
        handleInput()
        game.batch.begin()
        for (i in 0 until twoDArray.size) {
            for (j in 0 until twoDArray[i].size) {
                if (i % 2 == 0 && j % 2 == 0) {
                    game.batch.draw(grassTexture, j * hex * 0.75f, i * hex - (j % 2) * hex / 2, hex, hex)
                } else if (i % 2 == 0 && j % 2 == 1) {
                    game.batch.draw(pinkTexture, j * hex * 0.75f, i * hex - (j % 2) * hex / 2, hex, hex)
                } else if (i % 2 == 1 && j % 2 == 0) {
                    game.batch.draw(waterTexture, j * hex * 0.75f, i * hex - (j % 2) * hex / 2, hex, hex)
                } else {
                    game.batch.draw(beachTexture, j * hex * 0.75f, i * hex - (j % 2) * hex / 2, hex, hex)
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
