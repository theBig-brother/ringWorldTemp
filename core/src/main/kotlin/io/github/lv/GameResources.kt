package io.github.lv

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.czyzby.autumn.annotation.Component
import com.github.czyzby.autumn.annotation.Inject
import io.github.lv.entity.gameUnit.component.UnitAppearanceComponent
import java.sql.Connection

@Component
class GameResources {
    lateinit var batch: SpriteBatch
    lateinit var viewport: Viewport
    lateinit var camera: OrthographicCamera
    lateinit var font: BitmapFont
    lateinit var conn: Connection
  @Inject  lateinit var game: RingWorldGame
}
