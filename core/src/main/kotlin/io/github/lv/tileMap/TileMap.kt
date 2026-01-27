package io.github.lv.tileMap

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import io.github.lv.Constant.TILE_PX
import io.github.lv.GameResources
import io.github.lv.ai.FindPath
import io.github.lv.ai.Graph
import io.github.lv.entity.EngineContainer
import io.github.lv.ui.somethingElse.GameAssets.texture
import java.io.File


class TileMap(
    val camera: OrthographicCamera,
    val gameResources: GameResources,
    mapName: String,
    val engines: EngineContainer,
    terrainConfig: TerrainConfig
) {
    //        val cobblesTexture: Texture by lazy { Texture("data/core/images/terrain/cobbles-keep.png") }
    val cobblesTexture = texture("data/core/images/terrain/cobbles-keep.png")
    var mapHeight: Int = 0 // 地图总行数
    var mapWidth: Int = 0
    var mapMatrix: Array<Array<TileNode?>> = arrayOf()
    var findPath: FindPath? = null
    var visitedMatrix: Array<BooleanArray> = arrayOf()
    var blockedMatrix: Array<BooleanArray> = arrayOf()

    init {
//        val rows: List<List<String>> = csvReader().readAll(File("Home_1.csv"))
        val rows: List<List<String>> = csvReader().readAll(File(mapName))
        // rows[0] 就是第一行；rows[0][0] 就是第一行第一列
        mapHeight = rows.size
        mapWidth = rows.firstOrNull()?.size ?: 0
        mapMatrix = Array(mapHeight) { arrayOfNulls(mapWidth) }
        blockedMatrix = Array(mapHeight) { BooleanArray(mapWidth) }
        visitedMatrix = Array(mapHeight) { BooleanArray(mapWidth) }
        for (i in rows.indices) {
            for (j in rows[i].indices) {
                mapMatrix[i][j] = TileNode(j, i, i * mapWidth + j)
                mapMatrix[i][j]?.string =
                    rows[i][j].trim().takeIf { it.contains('^') }?.substringBefore('^') ?: rows[i][j].trim()
                mapMatrix[i][j]?.nodeTexture =
                    texture(paths = "data/core/images/terrain/" + terrainConfig.terrainSymbol[mapMatrix[i][j]?.string] + ".png")
            }
        }
        val graph = Graph(mapMatrix)
        findPath = FindPath(graph)
    }

    // draw map
    fun draw(delta: Float) {
        handleInput(delta)
        clampCamera()
        gameResources.batch.begin()
        for (i in 0 until mapHeight) {
            for (j in 0 until mapWidth) {
                mapMatrix[i][j]?.let {
                    val renderI = mapHeight - 1 - i
                    gameResources.batch.draw(
                        it.nodeTexture, j * TILE_PX * 0.75f, renderI * TILE_PX - (j % 2) * TILE_PX / 2, TILE_PX, TILE_PX
                    )
                }
            }
        }
        gameResources.batch.draw(
            cobblesTexture,
            0f,
            0f,
            cobblesTexture.width.toFloat(),
            cobblesTexture.height.toFloat()
        )
        gameResources.batch.end()
    }

    val mapWorldWidth = mapWidth * TILE_PX * 0.75f
    val mapWorldHeight = mapHeight * TILE_PX


    fun clampCamera() {
        // 1. 计算相机的可视窗口半宽/半高
//        val halfViewWidth = camera.viewportWidth * camera.zoom / 2f
//        val halfViewHeight = camera.viewportHeight * camera.zoom / 2f

        // 2. 限制的范围：相机中心点不能超出这些范围
        // 计算相机中心点允许的最大/最小值
//        val minX = halfViewWidth          // 最小X（允许左边界在这里）
//        val maxX = mapWorldWidth - halfViewWidth // 最大X（允许右边界在这里）
//        val minY = halfViewHeight        // 最小Y（允许下边界在这里）
//        val maxY = mapWorldHeight - halfViewHeight // 最大Y（允许上边界在这里）
        val minX = 0f          // 最小X（允许左边界在这里）
        val maxX = mapWorldWidth  // 最大X（允许右边界在这里）
        val minY = 0f        // 最小Y（允许下边界在这里）
        val maxY = mapWorldHeight  // 最大Y（允许上边界在这里）

        // 3. 如果地图小于视口，直接将相机固定在地图的中心
        // 如果地图宽度或高度小于视口宽度或高度，中心点应该保持在地图的中心
        if (mapWorldWidth <= camera.viewportWidth * camera.zoom) {
            camera.position.x = mapWorldWidth / 2f
        } else {
            // 否则将相机位置限制在允许的范围内
            camera.position.x = camera.position.x.coerceIn(minX, maxX)
        }

        if (mapWorldHeight <= camera.viewportHeight * camera.zoom) {
            camera.position.y = mapWorldHeight / 2f
        } else {
            camera.position.y = camera.position.y.coerceIn(minY, maxY)
        }
    }

    /*
    * 如果你下一步要做：

    边缘滚屏（鼠标贴边自动移动）

    Shift 加速 / 惯性平滑

    minimap 点击跳转相机

    相机缓动（文明那种
    * */
    fun handleInput(delta: Float) {

        val speedKey = 10f * TILE_PX
        val distKey = delta * speedKey
        //左移相机
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.translate(-distKey, 0f, 0f)
        }
        //右移相机
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.translate(distKey, 0f, 0f)
        }
        //下移相机
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.translate(0f, -distKey, 0f)
        }
        //上移相机
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.translate(0f, distKey, 0f)
        }
        //https://developer.aliyun.com/article/1619479
        // 鼠标中键拖拽地图
        if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {

//            val distMiddle = delta * 1f* TILE_PX不要自作聪明写这行
            val deltaX = -Gdx.input.deltaX * camera.zoom
            val deltaY = Gdx.input.deltaY * camera.zoom
            camera.translate(deltaX, deltaY, 0f)
        }

    }

    // 地图行 -> 渲染行（翻转）
    private fun toRenderRow(mapY: Int): Int = (mapHeight - 1 - mapY)

    // 渲染行 -> 地图行（翻转回去）
    private fun toMapI(renderRow: Int): Int = (mapHeight - 1 - renderRow)

    // 将六边形网格坐标转换为像素坐标
    fun mapToWorld(
        mapX: Int,  // 列（x）
        mapY: Int, // 行（y，向下）
        center: Boolean = true
    ): Pair<Float, Float> {
        val renderRow = toRenderRow(mapY)  // ⭐ 关键翻转
        return if (center) {
            val x = mapX * TILE_PX * 0.75f + TILE_PX / 2f
            val y = renderRow * TILE_PX - (mapX % 2) * TILE_PX / 2f + TILE_PX / 2f
            x to y
        } else {
            val x = mapX * TILE_PX * 0.75f
            val y = renderRow * TILE_PX - (mapX % 2) * TILE_PX / 2f
            x to y
        }
    }
    // 根据单位的屏幕坐标获取所在的六边形网格坐标
    /** 世界坐标 -> 地图格子（用于鼠标点选或人物位置） */
    fun worldToMap(worldX: Float, worldY: Float): Pair<Int, Int> {/*中心到左下角的偏移量在传入前计算
        *  计算网格坐标
        *  计算x坐标对应的网格列数，并四舍五入
        * */
        val mapX = (worldX / (TILE_PX * 0.75f)).toInt()
        // 计算y坐标对应的网格行数，偶数列需要偏移调整，并四舍五入
        // 偶数列和奇数列的偏移差异
        val renderRow = ((worldY + (mapX % 2) * TILE_PX / 2f) / TILE_PX).toInt()
        val mapY = toMapI(renderRow)
        return mapX to mapY
    }

    /** 边界检查也放这儿，别散落在各处
     * @param mapX 地图有多少列
     * @param mapY 地图有多少行
     * @return 在不在边界里面
     */
    fun inBounds(mapX: Int, mapY: Int): Boolean = mapX in 0 until mapWidth && mapY in 0 until mapHeight

}
