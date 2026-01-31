package io.github.lv.tileMap.inputProcessor

import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import io.github.lv.ecs.components.PositionComponent
import io.github.lv.ecs.pawn.state.OrderState
import io.github.lv.ecs.thing.component.ThingAppearanceComponent
import io.github.lv.ecs.thing.component.ThingInformationComponent
import io.github.lv.ecs.zone.component.ZoneNode
import io.github.lv.ui.somethingElse.GameAssets.texture
import ktx.graphics.use
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import io.github.lv.tileMap.axialToOddQ

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
                        mouse.waitForPending.add(entity)
                        //TODO根据类型决定
                        thingInformationComponent.workType = mouse.gameResources.orderState
                    } else {
                        mouse.waitForPending.remove(entity)
                        thingInformationComponent.workType = OrderState.UNKNOWN
                    }
                }
            }
        }
    },
    CONSTRUCTION {

    },
    WALL {
        override fun update(mouse: MouseStateMachine) {
            mouse.touchPos.set(
                Gdx.input.x.toFloat(),
                Gdx.input.y.toFloat()
            )
            mouse.gameResources.viewport.unproject(mouse.touchPos)
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                //画区域

            }
        }
    },
    ZONE {
        override fun update(mouse: MouseStateMachine) {
            mouse.touchPos.set(
                Gdx.input.x.toFloat(),
                Gdx.input.y.toFloat()
            )
            mouse.gameResources.viewport.unproject(mouse.touchPos)
            val centerMapPos = mouse.constructPlan.currentZone?.tileMap?.worldToMap(mouse.touchPos.x, mouse.touchPos.y)
            val zones = centerMapPos?.let {
                mouse.constructPlan.currentZone?.tileMap?.findAdjacentCell(
                    it.first,
                    it.second,
                    mouse.radius
                )
            }
            mouse.gameResources.batch.use(mouse.gameResources.camera) {
                val zoneTexture = texture(mouse.constructPlan.currentZone?.texturePath)
                if (zones != null) {
                    for (zone in zones) {
                        val zonePos =
                            axialToOddQ(zone?.let { it1 -> Pair(it1.first, it1.second) })
                        val drawPos =
                            mouse.constructPlan.currentZone?.tileMap?.mapToWorld(zonePos.first, zonePos.second)
                        if (mouse.constructPlan.currentZone?.tileMap?.inBounds(zonePos) == true) {
                            it.draw(
                                zoneTexture,
                                (drawPos?.first ?: -1f) - zoneTexture.width / 2,
                                (drawPos?.second ?: -1f) - zoneTexture.height / 2.0f
                            )
                        }
                    }
                }
            }
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                if (zones != null) {
                    for (zone in zones) {
                        val zonePos =
                            axialToOddQ(zone?.let { it1 -> Pair(it1.first, it1.second) })
                        val zoneNode = ZoneNode(zonePos.first, zonePos.second)
                        mouse.constructPlan.currentZone!!.zones.add(zoneNode)
                    }
                }
            }
        }
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
