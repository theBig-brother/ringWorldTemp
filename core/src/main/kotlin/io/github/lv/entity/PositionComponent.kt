package io.github.lv.entity

import com.badlogic.ashley.core.Component

// 位置组件，用来存储实体的坐标数据
data class PositionComponent(
    var mapX: Int = 0,// 网格坐标X
    var mapY: Int = 0 // 网格坐标Y
) : Component
