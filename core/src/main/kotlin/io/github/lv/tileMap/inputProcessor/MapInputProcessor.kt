package io.github.lv.tileMap.inputProcessor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.github.czyzby.autumn.annotation.Component
import com.github.czyzby.autumn.annotation.Inject
import io.github.lv.GameResources
import io.github.lv.ecs.EngineContainer
import ktx.app.KtxInputAdapter

@Component
class MapInputProcessor() : KtxInputAdapter {
    @Inject
    private lateinit var engines: EngineContainer      // 或者直接 Inject Engine，看你怎么封装
    private val unitEngine get() = engines.pawnEngine  // 和原来的参数一致

    @Inject
    private lateinit var gameResources: GameResources
    // 创建一个摄像机
    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        // 获取鼠标在世界坐标中的位置
        val screenX = Gdx.input.x.toFloat()
        val screenY = Gdx.input.y.toFloat()
        val worldBefore = gameResources.viewport.unproject(Vector2(screenX, screenY))
        // 计算新旧缩放比例,确保 zoom 的值不会太小或太大,等于coerceIn(0.5f, 3f)
        val newZoom = MathUtils.clamp(
            gameResources.camera.zoom + amountY * 0.1f,
            0.5f,
            3.0f
        )
        gameResources.camera.zoom = newZoom
        // 重新计算鼠标位置在缩放后的世界坐标
        // 注意：这里需要先将相机更新，然后重新计算unproject
        gameResources.camera.update()
        // 获取缩放后的相同屏幕位置对应的世界坐标
        val worldAfter = gameResources.viewport.unproject(Vector2(screenX, screenY))
        // 计算缩放前后鼠标世界坐标的偏移
        val dx = worldAfter.x - worldBefore.x
        val dy = worldAfter.y - worldBefore.y
        // 调整相机位置来补偿这个偏移，保持鼠标指向的世界位置不变
        gameResources.camera.position.x -= dx
        gameResources.camera.position.y -= dy
        gameResources.camera.update()
        return true
    }
}
