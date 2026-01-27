package io.github.lv.entity.pawn.system.walk

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.steer.SteerableAdapter
import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.ai.steer.SteeringBehavior
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import io.github.lv.Constant
import io.github.lv.entity.pawn.component.PawnAppearanceComponent
import io.github.lv.entity.pawn.component.PawnMotionComponent
import io.github.lv.extension.getCenter


class MyAgent(val pawnAppearanceComponent: PawnAppearanceComponent, val pawnMotionComponent: PawnMotionComponent) :
    SteerableAdapter<Vector2>() {
    override fun getPosition(): Vector2 {
        return pawnAppearanceComponent.unitSprite.getCenter()
    }

    override fun getOrientation(): Float = 0f
    override fun setOrientation(orientation: Float) {}

    override fun getLinearVelocity(): Vector2 = pawnMotionComponent.linearVelocity

    override fun getAngularVelocity(): Float = 0f

    override fun getMaxLinearSpeed(): Float = pawnMotionComponent.maxLinearSpeed
    override fun getMaxLinearAcceleration(): Float = pawnMotionComponent.maxLinearAcceleration
    override fun setMaxAngularSpeed(maxAngularSpeed: Float) {
        pawnMotionComponent.maxLinearSpeed = maxAngularSpeed
    }

    override fun setMaxLinearAcceleration(maxLinearAcceleration: Float) {
        pawnMotionComponent.maxLinearAcceleration = maxLinearAcceleration
    }

    override fun getMaxAngularSpeed(): Float = 0f
    override fun getMaxAngularAcceleration(): Float = 0f

    override fun isTagged(): Boolean = false
    override fun setTagged(tagged: Boolean) {}

    override fun vectorToAngle(vector: Vector2): Float = 0f
    override fun angleToVector(outVector: Vector2, angle: Float): Vector2 = outVector

    override fun getBoundingRadius(): Float = 0f

    override fun getZeroLinearSpeedThreshold(): Float = 0f
    override fun setZeroLinearSpeedThreshold(value: Float) {}

}
