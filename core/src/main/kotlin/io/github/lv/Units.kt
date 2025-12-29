package io.github.lv

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import kotlin.math.roundToInt

class Units(private val game: RingWorldGame, private val camera: OrthographicCamera) {
    val shamanTexture: Texture by lazy { Texture("shaman.png") }
    val demoTexture: Texture by lazy { Texture("amla-default.png") }
    val shamanSprite: Sprite = Sprite(shamanTexture) // Initialize the Sprite with the bucketTexture
    val hex = Constant.hexWidth
    val shapeRenderer = ShapeRenderer()

    // draw map
    fun draw() {
        shamanSprite.setSize(4f, 4f); // Define the size of the sprite
        shamanSprite.setPosition(0f, 0f) // Set the position of the sprite
        placeShamanOnGrid(2, 3)

        game.batch.begin()
        shamanSprite.x = 7.4f
        shamanSprite.y = 7.5f
        shamanSprite.draw(game.batch); // Sprites have their own draw method
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            val shamanX = shamanSprite.x
            val shamanY = shamanSprite.y
            val po = getGridPosition(shamanX, shamanY,false)
            println(po)
        }
        game.batch.end()
        // 启用ShapeRenderer
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)  // 使用线条模式
        // 设置ShapeRenderer的投影矩阵为相机的矩阵，应用相机的缩放、平移等变换
        shapeRenderer.projectionMatrix = camera.combined


        // 绘制精灵的边框，使用Sprite的x、y、width、height来表示边界
        shapeRenderer.rect(shamanSprite.x, shamanSprite.y, shamanSprite.width, shamanSprite.height)
        // 计算精灵的中心位置
        val centerX = shamanSprite.x + shamanSprite.width / 2
        val centerY = shamanSprite.y + shamanSprite.height / 2

        // 绘制一个小圆点，位置是精灵的中心
        shapeRenderer.circle(centerX, centerY, 0.2f)  // 5f 是圆点的半径

        // 结束ShapeRenderer绘制
        shapeRenderer.end()
    }

    // 将六边形网格坐标转换为像素坐标
    fun getHexPosition(gridX: Int, gridY: Int): Pair<Float, Float> {
        val x = gridX * hex * 0.75f
        val y = gridY * hex - (gridX % 2) * hex / 2
        return Pair(x, y)
    }

    // 更新萨满的位置，基于六边形网格坐标
    fun placeShamanOnGrid(gridX: Int, gridY: Int) {
        val (x, y) = getHexPosition(gridX, gridY)
        shamanSprite.setPosition(x, y)
    }

    // 根据萨满的屏幕坐标获取她所在的六边形网格坐标
    fun getGridPosition(x: Float, y: Float, leftBottom: Boolean = true): Pair<Int, Int> {
        var gridX = 0
        var gridY = 0
        if (leftBottom) {
            // 计算x坐标对应的网格列数，并四舍五入
            gridX = (x / (hex * 0.75f)).toInt()
            // 计算y坐标对应的网格行数，偶数列需要偏移调整，并四舍五入
            gridY = ((y + (gridX % 2) * hex / 2) / hex).toInt()

        } else {
            // 获取萨满的网格位置，从萨满的中心计算
            // 计算从中心到左下角的偏移量
            val adjustedX = x + hex / 2
            val adjustedY = y + hex / 2
            // 计算网格坐标
            gridX = (adjustedX / (hex * 0.75f)).toInt()
            // 偶数列和奇数列的偏移差异
            gridY = ((adjustedY + (gridX % 2) * hex / 2) / hex).toInt()
        }

        return Pair(gridX, gridY)
    }


}
