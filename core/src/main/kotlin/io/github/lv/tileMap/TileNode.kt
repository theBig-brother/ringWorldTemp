package io.github.lv.tileMap

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 *@param mapX x坐标，注意x与y和地图数组行列相反
 *@param mapY y坐标，注意x与y和地图数组行列相反
 * @param id 格子id
 * @param qr 轴坐标
 *
 */
data class TileNode(
    var mapX: Int,
    var mapY: Int,
    var id: Int
) {
    var cost = 1
    var nodeTexture: Texture? = null
    var string: String = ""
    var entities = mutableListOf<Entity?>()
    val qr: Pair<Int, Int>
        get() {
            val parity = mapX % 2
            val q = mapX
            val r = mapY - (mapX - parity) / 2
            return Pair(q, r)
        }
    private val _stateFlow = MutableStateFlow("Initial State")  // 初始状态
    val stateFlow: StateFlow<String> = _stateFlow

    // 发布者方法：通知状态变化
    fun updateState(newState: String) {
        _stateFlow.value = newState
    }
//    // 发布者方法：通知状态变化
//    fun updateState(newState: String) {
//        _stateFlow.value = newState
//    }
}

/**
 *@param hex 轴坐标
 * @return 偏移坐标，其中这个游戏坐标不会小于0所以为null返回-1
 */
fun axialToOddQ(hex: Pair<Int, Int>?): Pair<Int, Int> {
    if (hex == null) {
        return Pair(-1, -1)
    }
    val parity = hex.first and 1
    val col = hex.first
    val row = hex.second + (hex.first - parity) / 2
    return Pair(col, row)
}
