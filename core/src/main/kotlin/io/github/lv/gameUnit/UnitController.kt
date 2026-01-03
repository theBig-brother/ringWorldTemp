package io.github.lv.gameUnit

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import io.github.lv.Constant
import io.github.lv.extension.getCenter
import io.github.lv.extension.setCenterPosition

object UnitController {
    // 将六边形网格坐标转换为像素坐标
    fun getHexPosition(gridX: Int, gridY: Int, center: Boolean = true): Pair<Float, Float> {
        if (center) {
            val x = gridX * Constant.hexWidth * 0.75f + Constant.hexWidth / 2f
            val y = gridY * Constant.hexWidth - (gridX % 2) * Constant.hexWidth / 2 + Constant.hexWidth / 2f
            return Pair(x, y)
        } else {
            val x = gridX * Constant.hexWidth * 0.75f
            val y = gridY * Constant.hexWidth - (gridX % 2) * Constant.hexWidth / 2
            return Pair(x, y)
        }
    }

    // 根据单位的屏幕坐标获取所在的六边形网格坐标
    fun getGridPosition(x: Float, y: Float, center: Boolean = false): Pair<Int, Int> {
        var gridX: Int
        var gridY: Int
        if (center) {
            // 获取单位的网格位置，从单位的中心计算
            // 计算从中心到左下角的偏移量
            val adjustedX = x + Constant.hexWidth / 2
            val adjustedY = y + Constant.hexWidth / 2
            // 计算网格坐标
            gridX = (adjustedX / (Constant.hexWidth * 0.75f)).toInt()
            // 偶数列和奇数列的偏移差异
            gridY = ((adjustedY + (gridX % 2) * Constant.hexWidth / 2) / Constant.hexWidth).toInt()
        } else {
            // 计算x坐标对应的网格列数，并四舍五入
            gridX = (x / (Constant.hexWidth * 0.75f)).toInt()
            // 计算y坐标对应的网格行数，偶数列需要偏移调整，并四舍五入
            gridY = ((y + (gridX % 2) * Constant.hexWidth / 2) / Constant.hexWidth).toInt()
        }
        return Pair(gridX, gridY)
    }

    //fun isGridOccupied(gridX: Int, gridY: Int): Boolean {
//        // 检查该网格是否已经被占用
//        return units.unitSprite.x == gridX * Constant.hexWidth * 0.75f &&
//                units.unitSprite.y == gridY * Constant.hexWidth - (gridX % 2) * Constant.hexWidth / 2
//    }
// 将单位放置到六边形网格上的方法
    fun placeUnitOnGrid(unitSprite: Sprite, gridX: Int, gridY: Int, byCenter: Boolean = true) {
        if (byCenter) {
            val (x, y) = getHexPosition(gridX, gridY, true)//六边形格子变世界坐标
            unitSprite.setCenterPosition(x, y)// Set the position of the sprite
        } else {
            val (x, y) = getHexPosition(gridX, gridY, false)//六边形格子变世界坐标
            unitSprite.setPosition(x, y)// Set the position of the sprite
        }
    }

    fun moveToTarget(
        unit: GameUnit,
        targetX: Int,
        targetY: Int,
        delta: Float,
        speed: Float = 1f,
        arriveEps: Float = 0.05f
    ): Boolean {
        val targetPosition = getHexPosition(targetX, targetY, true)
        val dist: Float = Vector2(targetPosition.first, targetPosition.second).dst(unit.unitSprite.getCenter())
        if (dist < arriveEps) {
            unit.unitSprite.setCenterPosition(targetPosition.first, targetPosition.second)
            return true
        }
        val position = unit.unitSprite.getCenter()
        val dx = targetPosition.first - position.x
        val dy = targetPosition.second - position.y
        val cosθ = dx / dist
        val sinθ = dy / dist
        // 本帧最多移动 speed*delta
        val step = speed * delta
        val stepX: Float = step * cosθ
        val stepY: Float = step * sinθ
        // 防止越过目标（一步走太大）
        if (step * step >= dist) {
            unit.unitSprite.setCenterPosition(targetPosition.first, targetPosition.second)
            return true
        }
        unit.unitSprite.translateX(stepX)
        unit.unitSprite.translateY(stepY)
        return false
    }

    fun updateFollowPath(unit: GameUnit, delta: Float) {
        val path = unit.currentPath
        if (path.count == 0) return
        if (unit.pathIndex >= path.count) {
            unit.currentPath.clear()
            return
        }
        val nextNode = path.get(unit.pathIndex)
        val arrived = moveToTarget(unit, nextNode.j, nextNode.i, delta, speed = 3f)
        if (arrived) {
            // 到达一个格子后，更新逻辑格坐标（真相）
            unit.gridX = nextNode.j
            unit.gridY = nextNode.i
            unit.pathIndex++
        }
    }

}
