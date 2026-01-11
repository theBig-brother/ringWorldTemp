package io.github.lv.tileMap

import com.github.czyzby.autumn.annotation.Component
import com.github.czyzby.autumn.annotation.Inject


@Component
class MapManager {
    private val maps = mutableMapOf<String, MapContext>()
    private var current: MapContext? = null

    @Inject
    private lateinit var mapContextBuilder: MapContextBuilder
    fun add(mapId: String,mapName:String) {
        val mapContext = mapContextBuilder.build(mapId,mapName)
        maps[mapId] = mapContext
    }

    fun get(id: String) = maps[id]

    fun current() = current

    fun draw(delta: Float) {
        current?.tileMap?.draw(delta)
    }

    fun switch(id: String) {
        val next = maps[id] ?: error("map $id not found")
        current?.let { unload(it) }
        load(next)
        current = next
    }

    private fun load(map: MapContext) {
        if (!map.loaded) {
            // create engines, load tilemap, units, things...
            map.loaded = true
        }
    }

    private fun unload(map: MapContext) {
        // 清engine, 清entity, 清UI, 清输入
        map.loaded = false
    }
}


/*
* 如果你愿意，我能再给：

✔ 适配你现在 MapInputProcessor 的完整改造版
✔ MapContext + Units + Things 的实体组织方式
✔ UI 如何绑 current map
✔ autosave/room/层级/world 的未来扩展路线
✔ RimWorld/DF 的真实地图架构图（很有价值）

你要哪个？
*
* 如果你愿意，我还可以给：

✔ RimWorld地图管理结构图
✔ DF + Stellaris + Factorio地图层级比较
✔ 什么时候用 Array 更强
✔ 怎么让地图支持保存+热加载
✔ MapContext 如何绑定 Entities / Jobs / Fog-of-war
✔ 室内地图（建筑内部）怎么做
✔ 世界→局部地图切换的“跳层协议”

你要哪一个？
* */
