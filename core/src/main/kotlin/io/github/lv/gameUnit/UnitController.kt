package io.github.lv.gameUnit

import com.badlogic.gdx.graphics.g2d.Sprite
import io.github.lv.Constant

object UnitController {
    // 将六边形网格坐标转换为像素坐标
    fun getHexPosition(gridX: Int, gridY: Int): Pair<Float, Float> {
        val x = gridX * Constant.hexWidth * 0.75f
        val y = gridY * Constant.hexWidth - (gridX % 2) * Constant.hexWidth / 2
        return Pair(x, y)
    }

    // 将单位放置到六边形网格上的方法
    fun placeUnitOnGrid(unitSprite: Sprite, gridX: Int, gridY: Int) {
        val (x, y) = getHexPosition(gridX, gridY)
        unitSprite.setPosition(x, y)// Set the position of the sprite
    }
    // 根据单位的屏幕坐标获取所在的六边形网格坐标
    fun getGridPosition(x: Float, y: Float, leftBottom: Boolean = true): Pair<Int, Int> {
        var gridX: Int
        var gridY: Int
        if (leftBottom) {
            // 计算x坐标对应的网格列数，并四舍五入
            gridX = (x / (Constant.hexWidth * 0.75f)).toInt()
            // 计算y坐标对应的网格行数，偶数列需要偏移调整，并四舍五入
            gridY = ((y + (gridX % 2) * Constant.hexWidth / 2) / Constant.hexWidth).toInt()
        } else {
            // 获取单位的网格位置，从单位的中心计算
            // 计算从中心到左下角的偏移量
            val adjustedX = x + Constant.hexWidth / 2
            val adjustedY = y + Constant.hexWidth / 2
            // 计算网格坐标
            gridX = (adjustedX / (Constant.hexWidth * 0.75f)).toInt()
            // 偶数列和奇数列的偏移差异
            gridY = ((adjustedY + (gridX % 2) * Constant.hexWidth / 2) / Constant.hexWidth).toInt()
        }
        return Pair(gridX, gridY)
    }
}
