package io.github.lv.entity.pawn.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.ai.steer.limiters.LinearSpeedLimiter
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import io.github.lv.entity.PositionComponent
import io.github.lv.extension.getCenter
import io.github.lv.tileMap.TileMap
import ktx.ashley.Mapper

/**
 * 外观组件，用来存储单位的纹理和精灵.
 * */
class PawnAppearanceComponent : Component {
    companion object : Mapper<PawnAppearanceComponent>()
    lateinit var unitTexturePath: String  // 单位的纹理路径

    lateinit var unitSprite: Sprite   // 单位的精灵（用于渲染）
    var currentMap: TileMap? = null // 当前地图的指针
    var jump = true
    var animate: MutableList<Texture> = mutableListOf()
    var linearSpeed: Vector2? = Vector2()

    //type = GameUnitType.SHAMAN
//    unitSprite.setOriginCenter()
    // 通过unitSprite来获取gridX, gridY
    val position: PositionComponent
        get() {
            // 根据 unitSprite 计算 gridX 和 gridY
            return currentMap?.let {
             val center=   unitSprite.getCenter()
                val (gridX, gridY) = it.worldToMap(center.x, center.y)
                PositionComponent(gridX, gridY)
            } ?: throw IllegalStateException("Map not initialized")
        }
}
