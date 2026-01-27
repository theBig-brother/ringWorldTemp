package io.github.lv.entity.pawn.system.work

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.branch.Selector

class WorkBT(entity: Entity) {
    val tree: BehaviorTree<Entity>

    init {
        val root = Selector<Entity>()

//        val chaseSeq = Sequence<Entity>()
//        chaseSeq.addChild(IsPlayerNear())
//        chaseSeq.addChild(MoveToPlayer())
//
//        root.addChild(chaseSeq)
//        root.addChild(Wander())

        tree = BehaviorTree(root, entity)
    }

    fun update() {
        tree.step()
    }

}
