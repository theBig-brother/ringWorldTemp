package io.github.lv.ecs.pawn.system.walk

import com.badlogic.gdx.ai.utils.Location
import com.badlogic.gdx.math.Vector2

class TargetLocation(val pos: Vector2) : Location<Vector2> {
    override fun getPosition(): Vector2 = pos
    override fun getOrientation(): Float = 0f
    override fun setOrientation(orientation: Float) {}
    override fun vectorToAngle(vector: Vector2): Float = 0f
    override fun angleToVector(outVector: Vector2, angle: Float): Vector2 = outVector
    override fun newLocation(): Location<Vector2?>? {
        return null
    }
}
