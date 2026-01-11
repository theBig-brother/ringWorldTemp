package io.github.lv.tileMap

import com.github.czyzby.autumn.annotation.Component
import com.github.czyzby.autumn.annotation.Initiate
import io.github.lv.removeCommentLines
import java.io.File

@Component
class TerrainConfig {
    val terrainSymbol: MutableMap<String, String> = mutableMapOf()

    @Initiate
    fun load() {
        val content: String = File("terrain.cfg").readText()
        val cleanedText = removeCommentLines(content)
        val regex1 = Regex("""\[terrain_type](.*?)\[/terrain_type]""", RegexOption.DOT_MATCHES_ALL)
        val regex2 = Regex("""string=[^ \t\n]*""")
        val regex3 = Regex("""symbol_image=[^ \t\n]*""")
        val allMatches = regex1.findAll(cleanedText)
        allMatches.forEach { match ->
            // match.value 是整个匹配（包括标签）
            // match.groupValues[1] 是第一个捕获组的内容（不包括标签）
            val innerContent = match.groupValues[1]// 只包含中间的内容
            val string = regex2.find(innerContent)
            val symbolImage = regex3.find(innerContent)
            if (string != null && symbolImage != null) {
                val key = string.value.substringAfter('=', "").trim()
                val value = symbolImage.value.substringAfter('=', "").trim()
                if (key.isNotEmpty() && value.isNotEmpty()) {
                    terrainSymbol[key] = value
                }else {
                    println("Warning: key='$key', value='$value' - One or both are empty")
                }
            }
        }
    }
}
