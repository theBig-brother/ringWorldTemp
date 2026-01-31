package io.github.lv.ecs.pawn.system

import io.github.lv.ecs.EngineContainer

data class PawnAndEngine(
    val pawn: com.badlogic.ashley.core.Entity,
    val engineContainer: EngineContainer

)
