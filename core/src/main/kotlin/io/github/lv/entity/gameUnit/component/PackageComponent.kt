package io.github.lv.entity.gameUnit.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.pfa.DefaultGraphPath
import com.badlogic.gdx.ai.pfa.GraphPath
import com.badlogic.gdx.utils.Array
import io.github.lv.tileMap.TileNode


class PackageComponent : Component {
 var packageArray = Array<Entity>()
}
