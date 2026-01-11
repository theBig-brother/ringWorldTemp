package io.github.lv.entity.gameUnit.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import io.github.lv.entity.PositionComponent
import io.github.lv.tileMap.TileMap

// 外观组件，用来存储单位的纹理和精灵

class UnitAppearanceComponent : Component {
    lateinit var unitTexture: Texture  // 单位的纹理
    lateinit var unitSprite: Sprite   // 单位的精灵（用于渲染）
    var currentMap: TileMap? = null // 当前地图的指针

    //type = GameUnitType.SHAMAN
//    unitSprite.setOriginCenter()
    // 通过unitSprite来获取gridX, gridY
    val position: PositionComponent
        get() {
            // 根据 unitSprite 计算 gridX 和 gridY
            return currentMap?.let {
                val (gridX, gridY) = it.worldToMap(unitSprite.x, unitSprite.y)
                PositionComponent(gridX, gridY)
            } ?: throw IllegalStateException("Map not initialized")
        }
}
