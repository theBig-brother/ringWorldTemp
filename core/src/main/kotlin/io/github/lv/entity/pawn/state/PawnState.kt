package io.github.lv.entity.pawn.state

import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram
import io.github.lv.entity.pawn.system.BehaviorTrees
import io.github.lv.entity.pawn.system.PawnAndThing

enum class PawnState : State<PawnAndThing> {

    /*
    *     Idle,
    Walking,
    Working,
    Sleeping,
    Eating,
    Fleeing,
    DRAFT
    *  UNKNOWN,IDLE,DRAFT,CRAFTING,DESTORYING
    * */
    IDLE {
        override fun enter(pawnAndThing: PawnAndThing) {
            BehaviorTrees.setIdleTree(pawnAndThing.pawn)
        }
    },

    MOVE {
        override fun enter(pawnAndThing: PawnAndThing) {
            BehaviorTrees.setMoveTree(pawnAndThing.pawn)
        }
    },
    WORK {
        override fun enter(pawnAndThing: PawnAndThing) {
            BehaviorTrees.setWorkTree(pawnAndThing.pawn)
        }
    },
    FindWorkState {
        override fun enter(pawnAndThing: PawnAndThing) {
            BehaviorTrees.setFindWorkTree(pawnAndThing)
        }
    };

    override fun update(pawnAndThing: PawnAndThing) {}
    override fun exit(pawnAndThing: PawnAndThing) {}
    override fun onMessage(pawnAndThing: PawnAndThing, telegram: Telegram?) = false
}
