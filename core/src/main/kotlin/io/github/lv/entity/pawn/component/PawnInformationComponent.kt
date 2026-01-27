package io.github.lv.entity.pawn.component

import com.badlogic.ashley.core.Component
import io.github.lv.pawn.PawnWorkPriority
import ktx.ashley.Mapper

/**
 * 存储pawn的信息.
 * */
class PawnInformationComponent

    : Component {
    companion object : Mapper<PawnInformationComponent>()

    var id: Int = 0
    var name: String = ""
    var age: Int = 0
}

