package io.github.lv.ecs.pawn.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.ai.steer.SteeringBehavior
import com.badlogic.gdx.math.Vector2
import io.github.lv.Constant
import ktx.ashley.Mapper

class PawnMotionComponent : Component {
    companion object : Mapper<PawnMotionComponent>()
    val linearVelocity = Vector2()
    var angularVelocity = 0f
    var independentFacing = false
    var orientationOffset = 0f
    var steeringBehavior: SteeringBehavior<Vector2>? = null
    // ====== 配置参数 ======
    var maxLinearSpeed = 1f * Constant.TILE_PX
    var maxLinearAcceleration = 0.2f * Constant.TILE_PX

}
