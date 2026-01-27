package io.github.lv.pawn

data class Pawn(
    val id: Int,
    val name: String,
//    var position: TilePos,
    val workPriorities: MutableList<PawnWorkPriority>,
//    var state: PawnState = PawnState.Idle,
    var currentJobId: Int? = null,
    var pawnSkill:PawnSkill
)
