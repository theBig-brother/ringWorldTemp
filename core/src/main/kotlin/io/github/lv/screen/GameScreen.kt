package io.github.lv.screen

import com.badlogic.gdx.ScreenAdapter
import com.github.czyzby.autumn.annotation.Component
import com.github.czyzby.autumn.annotation.Inject
import io.github.lv.GameResources
import io.github.lv.entity.thing.component.ThingUnitType
import io.github.lv.tileMap.MapManager
import java.io.File
import java.sql.DriverManager
import kotlin.use

@Component
class GameScreen : ScreenAdapter() {
    @Inject
    private lateinit var mapScreen: MapScreen
    @Inject
    private lateinit var mapManager: MapManager
    @Inject
    private lateinit var gameResources: GameResources
    override fun show() {
        table()
        mapManager.add("home","Home_1.csv")
        gameResources.game.setScreen(mapScreen)
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
        gameResources.conn = DriverManager.getConnection(
            "jdbc:sqlite:${dbFile.absolutePath}/save1.db"
        )
        // 3. 创建表（SQL）
        var createTableSql = """
        CREATE TABLE IF NOT EXISTS gameUnits (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            age INTEGER,
            unitTexture VARCHAR,
            health INTEGER,
            mapX INTEGER,
            mapY INTEGER,
tileMapId string
        )
    """.trimIndent()

        gameResources.conn.createStatement().use { stmt ->
            stmt.execute(createTableSql)
        }
        createTableSql = """
        CREATE TABLE IF NOT EXISTS tileMaps (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL
        )
        """.trimIndent()
        gameResources.conn.createStatement().use { stmt ->
            stmt.execute(createTableSql)
        }
        createTableSql = """
        CREATE TABLE IF NOT EXISTS things (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            health FLOAT NOT NULL,
            unitTexture VARCHAR,
            mapX INTEGER,
            mapY INTEGER,
            unitType VARCHAR,
            tileMapId String
        )
        """.trimIndent()
        gameResources.conn.createStatement().use { stmt ->
            stmt.execute(createTableSql)
        }

//        insertDemo()
    }

    fun insertDemo(){
        // 4. 插入数据
        val insertSql1 = "INSERT INTO gameUnits (name, age,unitTexture,health,mapX,mapY,tileMapId) VALUES (?,?,?,?, ?,?,?)"
        gameResources.conn.prepareStatement(insertSql1).use { ps ->
            ps.setString(1, "Alice")
            ps.setInt(2, 18)
            ps.setString(3, "data/core/images/unit/shaman.png")
            ps.setInt(4, 100)
            ps.setInt(5, 1)
            ps.setInt(6, 23)
            ps.setString(7,"home")
            ps.executeUpdate()
            ps.setString(1, "Bob")
            ps.setInt(2, 20)
            ps.setString(3, "data/core/images/unit/spearman.png")
            ps.setInt(4, 100)
            ps.setInt(5, 0)
            ps.setInt(6, 22)
            ps.setString(7,"home")
            ps.executeUpdate()
        }
        // 4. 插入数据
        val insertSql2 = "INSERT INTO things (name, health,unitTexture,mapX,mapY,unitType,tileMapId) VALUES (?,?,?,?,?,?,?)"
        gameResources.conn.prepareStatement(insertSql2).use { ps ->
            ps.setString(1, "Tree1")
            ps.setFloat(2, 100f)
            ps.setString(3, "data/core/images/terrain/forest/great-tree.png")
            ps.setInt(4, 3)
            ps.setInt(5, 19)
            ps.setString(6, ThingUnitType.TREE.name)
            ps.setString(7, "home")
            ps.executeUpdate()
        }
    }
}
