package io.github.lv.screen

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.ScreenViewport
import io.github.lv.RingWorldGame
import io.github.lv.gameUnit.*
import io.github.lv.io.github.lv.ui.GameAssets.texture
import io.github.lv.io.github.lv.ui.UiDrawables
import io.github.lv.tileMap.MapInputProcessor
import io.github.lv.tileMap.TileMap
import java.sql.Connection


class MapScreen(
    val game: RingWorldGame,
    val camera: OrthographicCamera,
    val conn: Connection,
    val gameEngine: GameEngine
) : ScreenAdapter() {
    var music: Music? = null
    var skin: Skin = Skin(Gdx.files.internal("uiskin.json"))
    val viewport by lazy { ScreenViewport(camera) }
    val tileMap = TileMap(game, camera)
    val multiplexer = InputMultiplexer()        // 创建多重输入处理器
    val uiStage: Stage by lazy { Stage(ScreenViewport()) }
    val uiDrawables = UiDrawables()
    var selectedUnit: Entity? = null
    var unitPortrait: Image = Image()      // 先不给 drawable
    val renderSystem = RenderSystem(game.batch, camera, gameEngine.gameEngine)
    val mapInputProcessor = MapInputProcessor(
        camera,
        viewport,
        tileMap,
        gameEngine.gameEngine,
        getSelectedUnit = { selectedUnit }
    ) { unit ->
        selectedUnit = unit
        updateUnitPortrait(unit)
    }

    fun updateUnitPortrait(entity: Entity?) {
        if (entity == null) {
            unitPortrait.drawable = null
            return
        }
        val appearance: AppearanceComponent = entity.getComponent(AppearanceComponent::class.java)
        unitPortrait.drawable = TextureRegionDrawable(
            TextureRegion(appearance.unitTexture)
        )
    }

    override fun show() {
        multiplexer.addProcessor(uiStage)          // UI优先
        multiplexer.addProcessor(mapInputProcessor)    // 然后是游戏输入
//        multiplexer.addProcessor(gameInput)      // 最后是游戏输入
        Gdx.input.inputProcessor = multiplexer
        ui()
        findUnits()
    }

    fun findUnits() {

        //  查询数据
        val querySql = "SELECT id, name, age,unitTexture,startX,startY FROM gameUnits"
        conn.createStatement().use { stmt ->
            val rs = stmt.executeQuery(querySql)
            while (rs.next()) {
                gameEngine.createHumanEntity(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("age"),
                    texture(rs.getString("unitTexture")),
                    tileMap,
                    rs.getInt("startX"),
                    rs.getInt("startY")
                )
            }
        }
        unitPortrait.setScaling(Scaling.fit)
//        selectedUnit = units[0] // 默认选中一个（可选）
//        updateUnitPortrait(selectedUnit)
    }

    override fun render(delta: Float) {
        logic(delta)
        draw(delta)
        //在每帧渲染stage
        uiStage.act(Gdx.graphics.deltaTime.coerceAtMost(1 / 30f)) // 更新场景
        uiStage.draw()  // 绘制场景

    }

    override fun resize(width: Int, height: Int) {
        // 更新stage视图
        uiStage.viewport?.update(width, height, true)
        viewport.update(width, height, true)
    }

    fun colorDrawable(color: Color): Drawable {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(color)
        pixmap.fill()
        val texture = Texture(pixmap)
        pixmap.dispose()
        return TextureRegionDrawable(TextureRegion(texture))
    }

    fun logic(delta: Float) {

        val movementSystem: MovementSystem = gameEngine.gameEngine.getSystem(MovementSystem::class.java)
        movementSystem.update(delta)
    }

    fun draw(delta: Float) {
        // 清空屏幕
        ScreenUtils.clear(Color.GRAY)
        camera.update()
        game.batch.projectionMatrix = viewport.camera.combined
//        spriteBatch.projectionMatrix = viewport.camera.combined
        tileMap.draw(delta)
        renderSystem.update(delta)
//        for (unit in units) {
//            // 使用装饰器包装:
//            val decorated = UnitDrawDebugDecorator(unit, camera,tileMap)
//            decorated.draw(delta)
//
////            if (unit == selectedUnit) {
////                unit.drawSelectionCircle() // 你可以画圈 / 外框 / 高亮
////            }
//        }

    }

    fun ui() {
        uiStage.isDebugAll = true
//        Gdx.input.inputProcessor = uiStage

        // 你可以换成自己的 skin.json；这里用最省事的方式：自己塞 drawables
//        skin = Skin()
//        skin.add("default-font", BitmapFont())
//
//        // 统一的默认 Label 样式
//        skin.add("default", Label.LabelStyle(skin.getFont("default-font"), Color.WHITE))
//        skin.add("default", TextButton.TextButtonStyle().apply {
//            font = skin.getFont("default-font")
//            up = TextureRegionDrawable(TextureRegion(Texture("ui_btn_up.png")))
//            down = TextureRegionDrawable(TextureRegion(Texture("ui_btn_down.png")))
//        })
        // 一些贴图占位（你自己改文件名即可）

        // 根表：全屏
        val root = Table()
        root.setFillParent(true)

        // ===== 顶栏 =====
        val topBar = Table()
//        topBar.background = topBarBg
        topBar.background = colorDrawable(Color(0f, 0f, 0f, 0.6f)) // 半透明黑

        val btnMenu = TextButton("menu", skin)
        val btnAction = TextButton("action", skin)

        // 资源条（随便放几个 label 做占位）
        val resRow = Table()
        resRow.add(Label("⚔ 1", skin)).padRight(12f)
        resRow.add(Label("💰 0", skin)).padRight(12f)
        resRow.add(Label("🏠 0/2", skin)).padRight(12f)
        resRow.add(Label("👤 6", skin)).padRight(12f)
        resRow.add(Label("⛏ -4(4)", skin)).padRight(12f)
        resRow.add(Label("✚ 0", skin)).padRight(12f)
        resRow.add(Label("📘 40%", skin)).padRight(12f)
        resRow.add(Label("⏱ 17:17", skin)).padRight(12f)


        topBar.add(btnMenu).padLeft(8f).padTop(4f).padBottom(4f)
        topBar.add(btnAction).padLeft(6f).padTop(4f).padBottom(4f)
        topBar.add(resRow).expandX().left().padLeft(16f)
        // ===== 右侧面板 =====
        val rightPanel = Table()
        rightPanel.top().right()
//        rightPanel.background = rightPanelBg
        rightPanel.background = colorDrawable(Color(255f, 255f, 0f, 0.6f)) // 半透明黑

        // 头像框

        unitPortrait.setScaling(Scaling.fit)
        unitPortrait.drawable = uiDrawables.portraitImg // 可选


        // 小地图下方一排按钮（用 ImageButton 更像图标）
        fun iconButton(name: String): ImageButton {
            // 你用自己的图：ui_icon_xxx.png
            val up = uiDrawables.icon(name)
            val style = ImageButton.ImageButtonStyle().apply { imageUp = up }
            return ImageButton(style)
        }

        val iconRow = Table()
        iconRow.add(iconButton("search")).size(26f).pad(2f)
        iconRow.add(iconButton("shield")).size(26f).pad(2f)
        iconRow.add(iconButton("home")).size(26f).pad(2f)
        iconRow.add(iconButton("people")).size(26f).pad(2f)
        iconRow.add(iconButton("grid")).size(26f).pad(2f)
        iconRow.add(iconButton("gear")).size(26f).pad(2f)
        // 坐标/地形信息
        val infoLine1 = Label("21,49", skin)
        val infoLine2 = Label("山岭 (山岭)", skin)
        // 横条 banner（你图里那个夕阳条）
        val banner = Image(uiDrawables.sunsetImg)
        banner.setScaling(Scaling.stretch)

        // 头像框 + 右侧属性文字
//        val portrait = Image(uiDrawables.portraitImg)
//        portrait.setScaling(Scaling.fit)
        val stats = Table()
        stats.add(Label("1/6", skin)).left().row()
        stats.add(Label("health", skin)).left().padTop(8f).row()
        stats.add(Label("experience", skin)).left().padTop(8f).row()
//        stats.add(Label("age${selectedUnit?.age}", skin)).left().padTop(8f).row()
        stats.add(Label("aaa", skin)).left().padTop(8f).row()

//        val unitRow = Table()
//        unitRow.add(portrait).size(72f, 72f).padRight(8f)
//        unitRow.add(stats).expandX().left()

        // 底部大按钮
        val endTurnStyle = TextButton.TextButtonStyle().apply {
            font = skin.getFont("default-font")
            up = uiDrawables.bottomBtnUp
            down = uiDrawables.bottomBtnDown
            fontColor = Color.WHITE
        }
        val endTurnBtn = TextButton("ed的", endTurnStyle)
        // 右侧面板拼装
        rightPanel.add(unitPortrait).width(220f).height(180f).pad(8f).row()
        rightPanel.add(iconRow).padLeft(8f).padRight(8f).left().row()
        rightPanel.add(infoLine1).padLeft(10f).padTop(6f).left().row()
        rightPanel.add(infoLine2).padLeft(10f).padTop(2f).left().row()
        rightPanel.add(banner).width(220f).height(40f).pad(8f).row()
        rightPanel.add(stats).padLeft(8f).padRight(8f).left().row()
        rightPanel.add().expandY().row() // 中间留空，撑开到下面
        rightPanel.add(endTurnBtn).width(220f).height(44f).pad(10f).bottom()
        val worldPlaceholder = Table()
        // ===== 根布局拼装：顶栏一行，下面一行(世界 + 右侧面板) =====
        root.add(topBar).growX().height(36f).colspan(2).row()
        root.add(worldPlaceholder).expand().fill()          // 世界区域吃掉所有剩余空间
        root.add(rightPanel).width(240f).growY().right().top()
        uiStage.addActor(root)
    }

    override fun hide() {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {
        uiStage.dispose()
    }
}
