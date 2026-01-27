package io.github.lv.ai

import com.badlogic.gdx.ai.pfa.Connection
import com.badlogic.gdx.ai.pfa.DefaultConnection
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph
import com.badlogic.gdx.utils.Array
import io.github.lv.tileMap.TileNode

class Graph(
    val nodeMatrix: kotlin.Array<kotlin.Array<TileNode?>>
) : IndexedGraph<TileNode> {
    val height: Int = nodeMatrix.size
    val width: Int = nodeMatrix[0].size

    override fun getConnections(fromNode: TileNode): Array<Connection<TileNode?>?> {
        val connections = Array<Connection<TileNode?>?>()
        val i = fromNode.mapY
        val j = fromNode.mapX
        // flat-top + row-offset (odd-q)
        // 这里 i 是“行”，j 是“列”
        val neighbors = arrayOf(
            // 偶数列：未下移
            arrayOf(
                -1 to 0,  // N
                -1 to -1,  // NW
                0 to -1,   // SW
                1 to 0,   // S
                0 to 1,   // SE
                -1 to 1,   // NE
            ),
            // 奇数列：这一列整体下移半格
            arrayOf(
                -1 to 0,  // N
                0 to -1, // NW
                1 to -1,  // SW
                1 to 0,   // S
                1 to 1,   // SE
                0 to 1,  // NE
            )
        )
        for ((di, dj) in neighbors[j % 2]) {
            val ni = i + di
            val nj = j + dj
            if (inBounds(ni, nj)) {
                val to = nodeMatrix[ni][nj] ?: continue
                val connection = DefaultConnection(fromNode, to)
                connections.add(connection)
            }
        }
        return connections
    }

    override fun getIndex(node: TileNode): Int {
        return node.id
    }

    override fun getNodeCount(): Int {
        return height * width
    }

    fun inBounds(i: Int, j: Int) = j in 0 until width && i in 0 until height

}
