package io.github.lv

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.FitViewport

class MapInputProcessor(private val camera: OrthographicCamera, private val viewport: FitViewport) : InputProcessor {

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        // 获取鼠标在世界坐标中的位置
        val screenX = Gdx.input.x.toFloat()
        val screenY = Gdx.input.y.toFloat()
        val worldBefore = viewport.unproject(Vector2(screenX, screenY))
        // 计算新旧缩放比例,确保 zoom 的值不会太小或太大,等于coerceIn(0.5f, 3f)
        val newZoom = MathUtils.clamp(
            camera.zoom + amountY * 0.1f,
            0.5f,
            3.0f
        )
        camera.zoom = newZoom
        // 重新计算鼠标位置在缩放后的世界坐标
        // 注意：这里需要先将相机更新，然后重新计算unproject
        camera.update()
        // 获取缩放后的相同屏幕位置对应的世界坐标
        val worldAfter = viewport.unproject(Vector2(screenX, screenY))
        // 计算缩放前后鼠标世界坐标的偏移
        val dx = worldAfter.x - worldBefore.x
        val dy = worldAfter.y - worldBefore.y
        // 调整相机位置来补偿这个偏移，保持鼠标指向的世界位置不变
        camera.position.x -= dx
        camera.position.y -= dy
        camera.update()
        return true
    }

    // 其他 InputProcessor 方法的实现（不做处理，返回 false）
    override fun keyDown(keycode: Int): Boolean = false
    override fun keyUp(keycode: Int): Boolean = false
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false
    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false
    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = false
    override fun mouseMoved(screenX: Int, screenY: Int): Boolean = false
    override fun keyTyped(character: Char): Boolean = false
    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false

}
