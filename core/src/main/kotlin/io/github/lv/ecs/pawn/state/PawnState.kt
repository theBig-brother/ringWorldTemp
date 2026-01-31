package io.github.lv.ecs.pawn.state

import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram
import io.github.lv.ecs.pawn.system.BehaviorTrees
import io.github.lv.ecs.pawn.system.PawnAndEngine

enum class PawnState : State<PawnAndEngine> {

    /**
     * Sleeping,
     * Eating,
     * Fleeing,
     * DRAFT
     * UNKNOWN,DRAFT,CRAFTING,DESTROYING
     */
    IDLE {
        override fun enter(pawnAndEngine: PawnAndEngine) {
            BehaviorTrees.setIdleTree(pawnAndEngine.pawn)
        }
    },
    MOVE {
        override fun enter(pawnAndEngine: PawnAndEngine) {
            BehaviorTrees.setMoveTree(pawnAndEngine.pawn)
        }
    },
    WORK {
        override fun enter(pawnAndEngine: PawnAndEngine) {
            BehaviorTrees.setWorkTree(pawnAndEngine)
        }
    },
    FindWorkState {
        override fun enter(pawnAndEngine: PawnAndEngine) {
            BehaviorTrees.setFindWorkTree(pawnAndEngine)
        }
    };

    override fun update(pawnAndEngine: PawnAndEngine) {}
    override fun exit(pawnAndEngine: PawnAndEngine) {}
    override fun onMessage(pawnAndEngine: PawnAndEngine, telegram: Telegram?) = false
}
