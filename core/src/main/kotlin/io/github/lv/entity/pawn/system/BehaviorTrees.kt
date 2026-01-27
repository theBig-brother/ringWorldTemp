package io.github.lv.entity.pawn.system

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.Task
import io.github.lv.entity.pawn.component.BehaviorComponent
import io.github.lv.entity.pawn.system.task.IdleTask

import com.badlogic.gdx.ai.btree.branch.Sequence
import io.github.lv.entity.pawn.system.task.FindWorkTask
import io.github.lv.entity.pawn.system.task.MoveToTargetTask
import io.github.lv.entity.pawn.system.task.work.MineTask
import io.github.lv.entity.pawn.system.task.work.PlantCutTask
import io.github.lv.entity.pawn.system.task.work.ReadyToWorkTask
import io.github.lv.entity.pawn.system.task.work.WhereToWorkTask
import io.github.lv.entity.pawn.component.WorkComponent
import io.github.lv.entity.pawn.system.task.work.ToIdleTask
import io.github.lv.pawn.WorkType.*

object BehaviorTrees {
    private val behaviorMapper = ComponentMapper.getFor(BehaviorComponent::class.java)
    fun setIdleTree(entity: Entity) {
        val root = Sequence<Entity>().apply {
            addChild(IdleTask())
        }
        val tree = BehaviorTree(root, entity)

        behaviorMapper[entity].tree = tree
    }

    fun setMoveTree(entity: Entity) {
        val root = Sequence<Entity>().apply {
            addChild(MoveToTargetTask())
        }
        val tree = BehaviorTree(root, entity)
        behaviorMapper[entity].tree = tree
    }

    fun setWorkTree(entity: Entity) {
        val root = Sequence<Entity>().apply {
            val workComponent = WorkComponent.Companion.mapper[entity]
            addChild(MoveToTargetTask())
            addChild(ReadyToWorkTask())
            val works=
            when (workComponent.workType) {
                Firefight -> {}
                Patient -> {}
                Doctor -> {}
                BedRest -> {}
                Childcare -> {}
                Basic -> {}
                Warden -> {}
                Handle -> {}
                Cook -> {}
                Hunt -> {}
                Construct -> {}
                Grow -> {}
                Mine -> {
                  MineTask()
                }

                PlantCut -> {
                   PlantCutTask()
                }

                Smith -> {}
                Tailor -> {}
                Art -> {}
                Craft -> {}
                Haul -> {}
                Clean -> {}
                DarkStudy -> {}
                Research -> {}
                Sleep -> {}
                null -> {}
            } as? Task<Entity>
            addChild(works)
            addChild(ToIdleTask())
        }
        val tree = BehaviorTree(root, entity)
        behaviorMapper[entity].tree = tree
    }

    fun setFindWorkTree(pawnAndThing: PawnAndThing) {
        val root = Sequence<Entity>().apply {
            addChild(FindWorkTask(pawnAndThing.thingEngine))
            addChild(WhereToWorkTask())
        }
        val tree = BehaviorTree(root, pawnAndThing.pawn)
        behaviorMapper[pawnAndThing.pawn].tree = tree
    }

}
