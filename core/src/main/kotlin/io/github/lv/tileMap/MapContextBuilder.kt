package io.github.lv.tileMap

import com.github.czyzby.autumn.annotation.Component
import com.github.czyzby.autumn.annotation.Inject
import io.github.lv.GameResources
import io.github.lv.entity.EngineContainer

@Component
class MapContextBuilder {
   @Inject lateinit var  gameResources: GameResources
   @Inject lateinit var  engines: EngineContainer
   @Inject lateinit var  terrainConfig: TerrainConfig
    fun build(mapId: String,mapName: String): MapContext {
        return MapContext(mapId, mapName,gameResources, engines, terrainConfig)
    }
}
