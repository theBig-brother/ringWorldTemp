package io.github.lv.entity.thing.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Texture
import io.github.lv.tileMap.TileMap

// 外观组件，用来存储单位的纹理和精灵

class ThingAppearanceComponent : Component {
    lateinit var thingTexturePath: String
    lateinit var thingTexture: Texture  // 单位的纹理
    var currentMap: TileMap? = null // 当前地图的指针
    fun dispose() {
        thingTexture.dispose()

    }
}
