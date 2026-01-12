package io.github.lv.entity.gameUnit.system

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.github.czyzby.autumn.annotation.Inject
import io.github.lv.GameResources
import io.github.lv.entity.EngineContainer
import io.github.lv.entity.gameUnit.component.UnitAppearanceComponent

// 渲染系统，用来渲染所有包含AppearanceComponent的实体
@com.github.czyzby.autumn.annotation.Component
class RenderSystem() : EntitySystem() {
    // 新增一个标志位来控制是否绘制调试信息
    @Inject
    private lateinit var camera: OrthographicCamera
    @Inject
    private lateinit var resources: GameResources
    @Inject
    private lateinit var engines: EngineContainer
    var debugMode: Boolean = false
    val shapeRenderer = ShapeRenderer()
    override fun update(deltaTime: Float) {
        val entities = engines.unitEngine.getEntitiesFor(Family.all(UnitAppearanceComponent::class.java).get())

        // 遍历并渲染每个实体的精灵
        for (entity in entities) {
            val unitAppearanceComponent = entity.getComponent(UnitAppearanceComponent::class.java)
            // 如果启用调试模式，绘制调试信息
            if (debugMode) {
                drawDebugInfo(unitAppearanceComponent.unitSprite, camera)
            }
            resources.batch.begin()
            unitAppearanceComponent.unitSprite.setOriginCenter()
            unitAppearanceComponent.unitSprite.draw(resources.batch)
            resources.batch.end()
        }

    }

    // 用来绘制调试信息，例如单位的选择框
    fun drawDebugInfo(unitSprite: Sprite, camera: OrthographicCamera) {
// 绘制选中的单位的框或者其他调试信息
// 这里你可以使用ShapeRenderer来绘制图形，例：画一个选择框
        // 可以用ShapeRenderer来画一个圆或者矩形围绕这个单位
        // 启用ShapeRenderer
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)  // 使用线条模式
// 可选：设置线条宽度
//        Gdx.gl.glLineWidth(20f)
        // 设置ShapeRenderer的投影矩阵为相机的矩阵，应用相机的缩放、平移等变换
        shapeRenderer.projectionMatrix = camera.combined
        // 绘制精灵的边框，使用Sprite的x、y、width、height来表示边界
        shapeRenderer.rect(unitSprite.x, unitSprite.y, unitSprite.width, unitSprite.height)
        // 计算精灵的中心位置
        val centerX = unitSprite.x + unitSprite.width / 2
        val centerY = unitSprite.y + unitSprite.height / 2
        // 绘制一个小圆点，位置是精灵的中心
        shapeRenderer.circle(centerX, centerY, 0.2f)  // 5f 是圆点的半径
        // 结束ShapeRenderer绘制
        shapeRenderer.end()

    }
}
