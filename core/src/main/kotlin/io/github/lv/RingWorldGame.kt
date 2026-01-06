package io.github.lv

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FitViewport
import io.github.lv.screen.MapScreen
import java.io.File
import java.sql.DriverManager
import java.sql.Connection
import io.github.lv.screen.GameScreen

// ./gradlew :lwjgl3:run --stacktrace --info
//./gradlew :lwjgl3:run --stacktrace --debug

class RingWorldGame : Game() {
    lateinit var batch: SpriteBatch

    val viewport by lazy { FitViewport(Constant.ViewportWidth, Constant.ViewportHeight) }
    val font by lazy { BitmapFont() }
    var terrainSymbol = mutableMapOf<String, String>()

    override fun create() {
        batch = SpriteBatch()
        terrain()
// use libGDX's default font,font has 15pt, but we need to scale it to our viewport by ratio of viewport height to screen height
        font.setUseIntegerPositions(false)
        font.getData()?.setScale(viewport.worldHeight / Gdx.graphics.height)
        //       this.setScreen(MainMenuScreen(this))
        this.setScreen(GameScreen(this))
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
        batch.dispose()
        font.dispose()
    }

    fun terrain() {
//  读取整个文件为字符串
        val content: String = File("terrain.cfg").readText()
        val cleanedText = removeCommentLines(content)
        val regex1 = Regex("""\[terrain_type](.*?)\[/terrain_type]""", RegexOption.DOT_MATCHES_ALL)
        val regex2 = Regex("""string=[^ \t\n]*""")
        val regex3 = Regex("""symbol_image=[^ \t\n]*""")
        val allMatches = regex1.findAll(cleanedText)
        allMatches.forEach { match ->
            // match.value 是整个匹配（包括标签）
            // match.groupValues[1] 是第一个捕获组的内容（不包括标签）
            val innerContent = match.groupValues[1]  // 只包含中间的内容
            val string = regex2.find(innerContent)
            val symbolImage = regex3.find(innerContent)
            if (string != null && symbolImage != null) {
                val key = string.value.substringAfter('=', "").trim()
                val value = symbolImage.value.substringAfter('=', "").trim()
                if (key.isNotEmpty() && value.isNotEmpty()) {
                    terrainSymbol[key] = value
//                    println("Added: '$key' -> '$value'")
                } else {
                    println("Warning: key='$key', value='$value' - One or both are empty")
                }
            }
        }
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
