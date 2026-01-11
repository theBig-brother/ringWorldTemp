package io.github.lv.entity.gameUnit.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.ai.pfa.GraphPath
import com.badlogic.gdx.ai.pfa.DefaultGraphPath
import io.github.lv.tileMap.TileNode

// 移动组件，用来存储与单位移动相关的数据

class MovementComponent : Component {
    var currentPath: GraphPath<TileNode> = DefaultGraphPath()  // 当前路径
    var pathIndex: Int = 0  // 当前路径索引
    var behavior: Behavior = Behavior.IDLE
}
