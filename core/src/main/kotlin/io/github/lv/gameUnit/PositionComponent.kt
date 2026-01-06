package io.github.lv.gameUnit

import com.badlogic.ashley.core.Component

// 位置组件，用来存储实体的坐标数据
data class PositionComponent(
    var gridX: Int = 0,// 网格坐标X
    var gridY: Int = 0 // 网格坐标Y
)
