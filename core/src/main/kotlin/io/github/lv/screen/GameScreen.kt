package io.github.lv.screen

import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import io.github.lv.RingWorldGame
import io.github.lv.gameUnit.GameEngine
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import kotlin.use

class GameScreen(val game: RingWorldGame) : ScreenAdapter() {
    val camera = OrthographicCamera()  // 创建一个摄像机
    val gameEngine = GameEngine(game, camera)
    lateinit var conn: Connection
    override fun show() {
        table()
        game.setScreen(MapScreen(game, camera, conn, gameEngine))
    }

    fun table() {
        // 1. 数据库文件（当前目录下生成 test.db）
        val dbFile = File("data")
        if (!dbFile.exists()) {
            val success = dbFile.mkdirs()
            println("创建目录: ${if (success) "成功" else "失败"}")
        }
//        Class.forName("org.sqlite.JDBC") //少数机型会用
        println(dbFile.absolutePath)
        // 2. 建立连接
        conn = DriverManager.getConnection(
            "jdbc:sqlite:${dbFile.absolutePath}/save1.db"
        )
        // 3. 创建表（SQL）
        var createTableSql = """
        CREATE TABLE IF NOT EXISTS gameUnits (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            age INTEGER,
            unitTexture string,
            health INTEGER

        )
    """.trimIndent()

        conn.createStatement().use { stmt ->
            stmt.execute(createTableSql)
        }
        createTableSql = """
        CREATE TABLE IF NOT EXISTS tileMaps (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL
        )
        """.trimIndent()
        conn.createStatement().use { stmt ->
            stmt.execute(createTableSql)
        }
        // 4. 插入数据
//        val insertSql = "INSERT INTO gameUnits (name, age,unitTexture,health) VALUES (?, ?,?,?)"
//        conn.prepareStatement(insertSql).use { ps ->
//            ps.setString(1, "Alice")
//            ps.setInt(2, 18)
//            ps.setString(3, "images/unit/shaman.png")
//            ps.setInt(4, 100)
//
//            ps.executeUpdate()
//            ps.setString(1, "Bob")
//            ps.setInt(2, 20)
//            ps.setString(3, "images/unit/spearman.png")
//            ps.setInt(4, 100)
//            ps.executeUpdate()
//        }

    }

}
