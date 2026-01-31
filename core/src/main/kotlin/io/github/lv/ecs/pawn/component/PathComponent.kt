package io.github.lv.ecs.pawn.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.ai.pfa.GraphPath
import com.badlogic.gdx.ai.pfa.DefaultGraphPath
import io.github.lv.tileMap.TileNode
import ktx.ashley.Mapper

/**
* 移动组件，用来存储与单位移动相关的数据.
*/
class PathComponent : Component {
    companion object: Mapper<PathComponent>()
    var currentPath: GraphPath<TileNode> = DefaultGraphPath()  // 当前路径
    var pathIndex: Int = 0  // 当前路径索引

    //    var mapPosition: MapPosition by Delegates.observable(MapPosition(-1, -1)) { prop, old, new ->
//        if (old != new) {
//
//        }
//    }
    var oldTargetPosition = TargetPosition(-1, -1)
    var targetPosition: TargetPosition = TargetPosition(-1, -1)


}

data class TargetPosition(var x: Int, var y: Int)
