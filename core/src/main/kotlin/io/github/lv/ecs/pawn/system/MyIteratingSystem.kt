package io.github.lv.ecs.pawn.system

import com.badlogic.ashley.core.*

/**
 * A simple EntitySystem that iterates over each entity and calls processEntity() for each entity every time the EntitySystem is
 * updated. This is really just a convenience class as most systems iterate over a list of entities.
 * @author Stefan Bachmann
 */
abstract class MyIteratingSystem
/**
 * Instantiates a system that will iterate over the entities described by the Family.
 * @param family The family of entities iterated over in this System
 */ @JvmOverloads constructor(
    /**
     * @return the Family used when the system was created
     */
    val family: Family?, priority: Int = 0
) : EntitySystem(priority) {
    /**
     * @return set of entities processed by the system
     */
    var entities = mutableListOf<Entity>()
        private set

    /**
     * Instantiates a system that will iterate over the entities described by the Family, with a specific priority.
     * @param family The family of entities iterated over in this System
     * @param priority The priority to execute this system with (lower means higher priority)
     */

    override fun addedToEngine(engine: Engine) {
        for (entity in engine.getEntitiesFor(family)) {
            entities.add(entity)
        }

        // 监听实体的添加
        engine.addEntityListener(family, object : EntityListener {
            override fun entityAdded(entity: Entity) {
//                Thread.dumpStack()
                family?.let {
                    //这里在试图规避多次添加同一个实体的问题
                    if (it.matches(entity) && !entities.contains(entity)) {
                        entities.add(entity)
                    }
                }
            }

            override fun entityRemoved(entity: Entity) {
                entities.remove(entity)
            }
        })
    }

    override fun removedFromEngine(engine: Engine?) {
        entities.clear()
    }

    override fun update(deltaTime: Float) {
        for (entity in entities) {
            processEntity(entity, deltaTime)
        }
    }

    /**
     * This method is called on every entity on every update call of the EntitySystem. Override this to implement your system's
     * specific processing.
     * @param entity The current Entity being processed
     * @param deltaTime The delta time between the last and current frame
     */
    protected abstract fun processEntity(entity: Entity, deltaTime: Float)
}
