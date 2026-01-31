package io.github.lv.ecs.pawn.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.ashley.core.Entity
import ktx.ashley.Mapper

class BehaviorComponent : Component{
    companion object : Mapper<BehaviorComponent>()
    var delta:Float = 0f
    var tree: BehaviorTree<Entity>? = null
}
