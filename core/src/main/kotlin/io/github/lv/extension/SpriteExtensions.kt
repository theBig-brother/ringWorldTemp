package io.github.lv.extension
 // 放到你的工具包中
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2

// Sprite 中心点操作的扩展函数
fun Sprite.setCenterPosition(x: Float, y: Float) {
    this.setOriginCenter()
    this.setPosition(x - this.width / 2, y - this.height / 2)
}

fun Sprite.setCenterPosition(vector: Vector2) {
    setCenterPosition(vector.x, vector.y)
}

fun Sprite.getCenterX(): Float = this.x + this.originX
fun Sprite.getCenterY(): Float = this.y + this.originY
fun Sprite.getCenter(): Vector2 = Vector2(getCenterX(), getCenterY())

// 快捷创建已设置中心的 Sprite
fun Texture.toCenteredSprite(): Sprite {
    return Sprite(this).apply {
        setOriginCenter()
    }
}

// 移动相关扩展
fun Sprite.moveCenterTo(x: Float, y: Float) {
    val currentCenterX = getCenterX()
    val currentCenterY = getCenterY()
    this.x += x - currentCenterX
    this.y += y - currentCenterY
}

fun Sprite.moveCenterBy(dx: Float, dy: Float) {
    this.x += dx
    this.y += dy
}

// 对齐操作
fun Sprite.centerBetween(x1: Float, y1: Float, x2: Float, y2: Float) {
    val centerX = (x1 + x2) / 2
    val centerY = (y1 + y2) / 2
    setCenterPosition(centerX, centerY)
}
