package io.github.lv.ecs.pawn.system

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.BehaviorTree
import io.github.lv.ecs.pawn.component.BehaviorComponent
import io.github.lv.ecs.pawn.system.task.IdleTask

import com.badlogic.gdx.ai.btree.branch.Sequence
import io.github.lv.ecs.pawn.system.task.FindWorkTask
import io.github.lv.ecs.pawn.system.task.MoveToTargetTask
import io.github.lv.ecs.pawn.system.task.work.MineTask
import io.github.lv.ecs.pawn.system.task.work.PlantCutTask
import io.github.lv.ecs.pawn.system.task.work.ReadyToWorkTask
import io.github.lv.ecs.pawn.system.task.work.WhereToWorkTask
import io.github.lv.ecs.pawn.component.WorkComponent
import io.github.lv.ecs.pawn.system.task.work.FindZoneTask
import io.github.lv.ecs.pawn.system.task.work.PickUpTask
import io.github.lv.ecs.pawn.system.task.work.PutDownTask
import io.github.lv.ecs.pawn.system.task.work.ToIdleTask
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

    fun setWorkTree(pawnAndEngine: PawnAndEngine) {
        val entity = pawnAndEngine.pawn
        val root = Sequence<Entity>().apply {
            val workComponent = WorkComponent.mapper[entity]
            addChild(MoveToTargetTask())
            addChild(ReadyToWorkTask())
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
                    addChild(MineTask())
                }

                PlantCut -> {
                    addChild(PlantCutTask())
                }

                Smith -> {}
                Tailor -> {}
                Art -> {}
                Craft -> {}
                Haul -> {
                    addChild(PickUpTask())
                    addChild(FindZoneTask(pawnAndEngine.engineContainer.zoneEngine))
                    addChild(MoveToTargetTask())
                    addChild(ReadyToWorkTask())
                    addChild(PutDownTask())
                }

                Clean -> {}
                DarkStudy -> {}
                Research -> {}
                Sleep -> {}
                null -> {}
            }
            addChild(ToIdleTask())
        }
        val tree = BehaviorTree(root, entity)
        behaviorMapper[entity].tree = tree
    }

    fun setFindWorkTree(pawnAndEngine: PawnAndEngine) {
        val root = Sequence<Entity>().apply {
            addChild(FindWorkTask(pawnAndEngine.engineContainer.thingEngine))
            addChild(WhereToWorkTask())
        }
        val tree = BehaviorTree(root, pawnAndEngine.pawn)
        behaviorMapper[pawnAndEngine.pawn].tree = tree
    }

}
