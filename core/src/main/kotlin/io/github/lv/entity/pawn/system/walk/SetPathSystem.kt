package io.github.lv.entity.pawn.system.walk

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.utils.TimeUtils
import io.github.lv.entity.pawn.component.PathComponent
import io.github.lv.entity.pawn.component.PawnAppearanceComponent
import io.github.lv.entity.pawn.system.MyIteratingSystem
import io.github.lv.tileMap.TileMap
import ktx.ashley.get

/**
 * 这个system是寻路用的.
 * <p>
 *     遍历entity，监听movementComponent.targetPosition,只要targetPosition与oldTargetPosition不一样就寻路。
 *     还有个功能，地图更新时候重新寻路防止原路径堵塞。
 * </p>
 */
class SetPathSystem() : MyIteratingSystem(
    Family.all(
        PawnAppearanceComponent::class.java,
        PathComponent::class.java
    ).get()
) {

    lateinit var tileMap: TileMap
        private set

    override fun processEntity(entity: Entity, deltaTime: Float) {
//        TODO("Not yet implemented")
        val appearance: PawnAppearanceComponent? = entity[PawnAppearanceComponent.Companion.mapper]
        val pathComponent = entity.getComponent(PathComponent::class.java)
        val position = appearance?.position
//TODO("NULL判断")
        tileMap = appearance?.currentMap!!
        val targetGridY = pathComponent.targetPosition.y
        val targetGridX = pathComponent.targetPosition.x
        if (tileMap.inBounds(targetGridY, targetGridX)) {
            if (pathComponent.oldTargetPosition != pathComponent.targetPosition) {
                //目标变化的时候才更新
                pathComponent.oldTargetPosition = pathComponent.targetPosition
                tileMap.findPath?.let {
                    if (position != null && it.findPathNode(
                            tileMap.mapMatrix[position.mapY][position.mapX],
                            tileMap.mapMatrix[targetGridY][targetGridX],
                            pathComponent.currentPath
                        )
                    ) {
                        pathComponent.pathIndex = 0
                        //TODO("别忘了删除")
                        for (i in pathComponent.currentPath) {
                            print("Grid(:${i.mapX},${i.mapY}) ")
                        }
                        println()
                    }
                }
            }
        }
        val currentTime = TimeUtils.nanoTime()
        if (lastTime == 0L) {
            lastTime = currentTime // 第一次调用时，初始化 lastTime
        }
        val elapsedTime = (currentTime - lastTime) / 1000000000.0f  // 将纳秒转为秒
        if (elapsedTime >= period) {
            lastTime = currentTime  // 更新 lastTime
            //  TODO:(地图更新寻路)
        }
    }

    val period = 0.1f

    // 保存上次更新时间
    var lastTime: Long = 0

}
