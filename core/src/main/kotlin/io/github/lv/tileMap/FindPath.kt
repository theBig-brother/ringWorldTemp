package io.github.lv.tileMap

import com.badlogic.gdx.ai.pfa.Connection
import com.badlogic.gdx.ai.pfa.DefaultGraphPath
import com.badlogic.gdx.ai.pfa.GraphPath
import com.badlogic.gdx.ai.pfa.Heuristic
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder
import java.util.Collections.emptyList

class FindPath(
    graph: Graph
) {
    val manhattanHeuristic = object : Heuristic<TileNode> {
        override fun estimate(node: TileNode, endNode: TileNode): Float {
            return (kotlin.math.abs(node.mapX - endNode.mapX) + kotlin.math.abs(node.mapY - endNode.mapY)).toFloat()
//        return 0f
        }
    }

    val pathFinder = IndexedAStarPathFinder(graph)
    fun findPathNode(start: TileNode?, target: TileNode?, path: GraphPath<TileNode>): Boolean {
        path.clear()
        val found = pathFinder.searchNodePath(start, target, manhattanHeuristic, path)
        return found
    }

    fun findPathConnection(
        start: TileNode?,
        target: TileNode?,
        out: GraphPath<Connection<TileNode>>
    ): Boolean {
        out.clear()
        val found = pathFinder.searchConnectionPath(start, target, manhattanHeuristic, out)
        return found
    }
}
