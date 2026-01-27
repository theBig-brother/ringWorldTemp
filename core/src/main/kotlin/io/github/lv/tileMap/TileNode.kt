package io.github.lv.tileMap

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class TileNode(
    var mapX : Int,
    var mapY: Int,
    var id: Int
) {
    var cost = 1
    var nodeTexture: Texture?= null
    var string:String = ""
    var entities = mutableListOf<Entity?>()

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
