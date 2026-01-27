package io.github.lv.entity.pawn.system

import com.badlogic.ashley.core.Engine

data class PawnAndThing(
    val pawn: com.badlogic.ashley.core.Entity,
    val thingEngine: Engine
)
