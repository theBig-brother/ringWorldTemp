package io.github.lv.ecs.thing.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Texture
import io.github.lv.ecs.zone.component.ZoneComponent
import io.github.lv.tileMap.TileMap
import ktx.ashley.Mapper

// 外观组件，用来存储单位的纹理和精灵

class ThingAppearanceComponent : Component {
    companion object : Mapper<ThingAppearanceComponent>()
    lateinit var thingTexturePath: String
    lateinit var thingTexture: Texture  // 单位的纹理
    var currentMap: TileMap? = null // 当前地图的指针
    var show = true
    fun dispose() {
        thingTexture.dispose()

    }
}
