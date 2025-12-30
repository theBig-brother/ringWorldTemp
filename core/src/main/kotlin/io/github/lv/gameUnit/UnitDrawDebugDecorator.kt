package io.github.lv.gameUnit

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.g2d.Sprite
import io.github.lv.RingWorldGame

class UnitDrawDebugDecorator(
    private val unit: GameUnit,
    override val game: RingWorldGame,
    val camera: OrthographicCamera,
) : GameUnit() {
    override val unitTexture: Texture by lazy { unit.unitTexture }
    override val unitSprite: Sprite by lazy { unit.unitSprite }
    val shapeRenderer = ShapeRenderer()
    override fun draw() {
        // 调用原有的绘制方法
        unit.draw()
        // 启用ShapeRenderer
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)  // 使用线条模式
        // 设置ShapeRenderer的投影矩阵为相机的矩阵，应用相机的缩放、平移等变换
        shapeRenderer.projectionMatrix = camera.combined
        // 绘制精灵的边框，使用Sprite的x、y、width、height来表示边界
        shapeRenderer.rect(unit.unitSprite.x, unit.unitSprite.y, unit.unitSprite.width, unit.unitSprite.height)
        // 计算精灵的中心位置
        val centerX = unit.unitSprite.x + unit.unitSprite.width / 2
        val centerY = unit.unitSprite.y + unit.unitSprite.height / 2
        // 绘制一个小圆点，位置是精灵的中心
        shapeRenderer.circle(centerX, centerY, 0.2f)  // 5f 是圆点的半径
        // 结束ShapeRenderer绘制
        shapeRenderer.end()
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            val unitX = unit.unitSprite.x
            val unitY = unit.unitSprite.y
            val po = UnitController.getGridPosition(unitX, unitY, false)
            println(po)
        }

//        // 添加调试绘制逻辑
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
//        shapeRenderer.projectionMatrix = camera.combined
//        // 假设这里是绘制单位的边框和中心点
//        shapeRenderer.rect(0f, 0f, 10f, 10f) // 示例
//        shapeRenderer.end()
    }
}
