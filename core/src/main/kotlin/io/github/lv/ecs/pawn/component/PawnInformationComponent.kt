package io.github.lv.ecs.pawn.component

import com.badlogic.ashley.core.Component
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

