package io.github.lv

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.czyzby.autumn.annotation.Component
import com.github.czyzby.autumn.annotation.Inject
import io.github.lv.ui.MouseState
import io.github.lv.ui.OrderState
import java.sql.Connection

@Component
class GameResources {
    lateinit var batch: SpriteBatch
    lateinit var viewport: Viewport
    lateinit var camera: OrthographicCamera
    lateinit var font: BitmapFont
    lateinit var conn: Connection
    lateinit var shapeRenderer: ShapeRenderer
    var orderState = OrderState.NULL
    var mouseState = MouseState.DEFAULT
    @Inject
    lateinit var game: RingWorldGame
}
