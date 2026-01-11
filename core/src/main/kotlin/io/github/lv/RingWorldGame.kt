package io.github.lv

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.github.czyzby.autumn.annotation.Component
import com.github.czyzby.autumn.annotation.Inject
import com.github.czyzby.autumn.context.ContextDestroyer
import com.github.czyzby.autumn.context.ContextInitializer
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.VisUI.SkinScale
import java.io.File
import io.github.lv.screen.GameScreen
import com.github.czyzby.autumn.fcs.scanner.DesktopClassScanner
import io.github.lv.entity.GameEngine

// ./gradlew :lwjgl3:run --stacktrace --info
//./gradlew :lwjgl3:run --stacktrace --debug
@Component
class RingWorldGame() : Game() {
    //    val viewport by lazy { FitViewport(Constant.ViewportWidth, Constant.ViewportHeight) }
    val viewport = ScreenViewport()

    private lateinit var gameResources: GameResources
    @Inject
    private lateinit var gameScreen: GameScreen
    private lateinit var destroyer: ContextDestroyer
    override fun create() {
// use libGDX's default font,font has 15pt, but we need to scale it to our viewport by ratio of viewport height to screen height
        // 加载支持中文的字体
        val fontGenerator = FreeTypeFontGenerator(Gdx.files.internal("ui/simhei.ttf"))
        val fontParameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        fontParameter.size = 24  // 设置字体大小
        fontParameter.flip = true  // 翻转字体，适用于一些字体生成

        //Autumn 入口
        val initializer = ContextInitializer()

        // 配置扫描（桌面 classpath 扫描器由 gdx-autumn-desktop-classgraph 提供）
        //告诉 Autumn：从 RingWorldGame 所在包开始，用这个 scanner
        initializer.scan(RingWorldGame::class.java, DesktopClassScanner()).doAfterInitiation { ctx ->
            gameResources = ctx.getComponent(GameResources::class.java) as GameResources
            gameResources.batch = SpriteBatch()
            // 生成字体
            gameResources.font = fontGenerator.generateFont(fontParameter)
            gameResources.font.setUseIntegerPositions(false)
            gameResources.camera=OrthographicCamera()
            gameResources.viewport = ScreenViewport(gameResources.camera)
            gameResources.game = this
            //resources.font.getData()?.setScale(viewport.worldHeight / Gdx.graphics.height)
            val gameEngine = ctx.getComponent(GameEngine::class.java) as GameEngine
            gameEngine.initialize()
            //下面这句是未知原因
            gameScreen=ctx.getComponent(GameScreen::class.java) as GameScreen
        }
        //启动 DI（这里才发生：@Component / @Inject / @Initiate）
        destroyer = initializer.initiate()
        VisUI.load(SkinScale.X1)

        this.setScreen(gameScreen)
        //        this.setScreen(MainMenuScreen(this))
//        debugPaths()
    }

    fun debugPaths() {
        println("=== 路径调试信息 ===")
        println("1. 用户目录: ${System.getProperty("user.home")}")
        println("2. 当前工作目录: ${System.getProperty("user.dir")}")
        println("3. 操作系统: ${System.getProperty("os.name")}")

        val testFile = File("data", "test.db")
        println("4. 相对路径: ${testFile.path}")
        println("5. 绝对路径: ${testFile.absolutePath}")
        println("6. 标准路径: ${testFile.canonicalPath}")
        println("7. 父目录: ${testFile.parent}")
        println("8. 父目录绝对路径: ${testFile.parentFile?.absolutePath}")
        println("9. 父目录是否存在: ${testFile.parentFile?.exists()}")
    }

    override fun render() {
        super.render() // important!
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
//        viewport!!.update(width, height, true)
    }

    override fun dispose() {
        gameResources.batch.dispose()
        gameResources.font.dispose()
        destroyer.dispose()
    }

//    fun loadNewSaveGame(saveData: SaveData) {
//        // 清除现有的实体和系统
//        engine.removeAllEntities()
//        engine.removeAllSystems()
//
//        // 重新加载新的存档数据
//        loadEntitiesFromSaveData(saveData)
//        loadSystemsForNewGame()
//
//        // 更新引擎，重新开始游戏逻辑
//        engine.update(0f)
//    }

}

fun removeCommentLines(text: String): String {
    // 使用正则表达式去除每行中 # 后的内容
    return text.lines().joinToString("\n") { it.replace(Regex("#.*"), "") }
}
