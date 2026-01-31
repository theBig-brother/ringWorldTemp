package io.github.lv.tileMap

import io.github.lv.GameResources
import io.github.lv.ecs.EngineContainer

class MapContext(
    val id: String,
    mapName: String,
    gameResources: GameResources,
    val engines: EngineContainer,
    terrainConfig: TerrainConfig,
    var loaded: Boolean = false
) {
    val tileMap: TileMap = TileMap(gameResources.camera, gameResources, mapName,engines, terrainConfig)
}
