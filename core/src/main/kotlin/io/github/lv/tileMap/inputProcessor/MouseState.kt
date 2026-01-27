package io.github.lv.tileMap.inputProcessor

import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import io.github.lv.entity.PositionComponent
import io.github.lv.entity.thing.component.ThingAppearanceComponent
import io.github.lv.entity.thing.component.ThingInformationComponent
import io.github.lv.entity.pawn.state.OrderState
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import ktx.graphics.*

enum class MouseState : State<MouseStateMachine> {
    //    ,ORDER,CONSTRUCTION,WALL,ZONE,FLOOR
    DEFAULT {
        override fun update(mouse: MouseStateMachine) {

        }

    },
    ORDER_RECT {

        override fun update(mouse: MouseStateMachine) {
            mouse.touchPos.set(
                Gdx.input.x.toFloat(),
                Gdx.input.y.toFloat()
            ) // Get where the touch happened on screen
            mouse.gameResources.viewport.unproject(mouse.touchPos)
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                mouse.gameResources.shapeRenderer.use(ShapeRenderer.ShapeType.Line, mouse.gameResources.camera) {
                    // Operate on shapeRenderer instance
                    // 绘制边框
                    mouse.gameResources.shapeRenderer.rect(
                        mouse.startRect.x, mouse.startRect.y,
                        mouse.touchPos.x - mouse.startRect.x, mouse.touchPos.y - mouse.startRect.y
                    )
                }
                //标记
                val rect = SelectionRect(mouse.startRect, mouse.touchPos)
                val entities =
                    mouse.engines.thingEngine.getEntitiesFor(Family.all(ThingAppearanceComponent::class.java).get())
                for (entity in entities) {
                    val thingInformationComponent = entity.getComponent(ThingInformationComponent::class.java)
                    val positionComponent = entity.getComponent(PositionComponent::class.java)
                    val x = mouse.mapManager.current()?.tileMap?.mapToWorld(
                        positionComponent.mapX,
                        positionComponent.mapY
                    )!!.first
                    val y = mouse.mapManager.current()?.tileMap?.mapToWorld(
                        positionComponent.mapX,
                        positionComponent.mapY
                    )!!.second
                    if (rect.contains(x, y)) {
                        thingInformationComponent.isPending = true
                        thingInformationComponent.workType = mouse.gameResources.orderState
                    }
                }
            }
        }
    },
    CONSTRUCTION {

    },
    WALL {

    },
    ZONE {

    },
    FLOOR {

    };

    override fun enter(mouse: MouseStateMachine) {}
    override fun update(mouse: MouseStateMachine) {}
    override fun exit(mouse: MouseStateMachine) {}
    override fun onMessage(mouse: MouseStateMachine, telegram: Telegram?) = false
}

data class SelectionRect(
    val startRect: Vector2, val touchPos: Vector2
) {
    val left: Float get() = min(startRect.x, touchPos.x)
    val right: Float get() = max(startRect.x, touchPos.x)
    val top: Float get() = min(startRect.y, touchPos.y)
    val bottom: Float get() = max(startRect.y, touchPos.y)
    val width: Float get() = abs(startRect.x - touchPos.x)
    val height: Float get() = abs(startRect.y - touchPos.y)
    fun contains(x: Float, y: Float): Boolean {
        return left <= x && x <= right && top <= y && y <= bottom
    }

    fun contains(point: Pair<Float, Float>): Boolean {
        return left <= point.first && point.first <= right && top <= point.second && point.second <= bottom
    }
}
