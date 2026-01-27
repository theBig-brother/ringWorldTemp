package io.github.lv.entity.pawn.system.render

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import io.github.lv.entity.pawn.component.PathComponent
import io.github.lv.entity.pawn.component.PawnAppearanceComponent
import io.github.lv.ui.somethingElse.GameAssets.texture
import com.badlogic.gdx.utils.TimeUtils
import com.github.czyzby.autumn.annotation.Component
import io.github.lv.entity.pawn.system.MyIteratingSystem

@Component
class AnimateSystem : MyIteratingSystem(
    Family.all(
        PawnAppearanceComponent::class.java,
        PathComponent::class.java
    ).get()
) {
    var index = 1
    val period = 0.1f
    // 保存上次更新时间
    var lastTime: Long = 0
    override fun update(deltaTime: Float) {
        val currentTime = TimeUtils.nanoTime()
        val elapsedTime = (currentTime - lastTime) / 1000000000.0f  // 将纳秒转为秒
        if (elapsedTime >= period) {
            lastTime = currentTime  // 更新 lastTime
            super.update(deltaTime)
        }
    }

    //TODO肯定不是这个动画实现
    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (lastTime == 0L) {
            lastTime = TimeUtils.nanoTime()  // 初始化 lastTime
        }

        val appearance = entity.getComponent(PawnAppearanceComponent::class.java)

        if (appearance.jump) {
            ++index
            appearance.unitSprite.setTexture(texture("data/core/images/units/elves-wood/shaman-heal${index % 9 + 1}.png"))
        } else {
            appearance.unitSprite.texture = texture("data/core/images/unit/shaman.png")
        }

    }
}
