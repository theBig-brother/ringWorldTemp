// GameAssets.kt
package io.github.lv.io.github.lv.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

object GameAssets{
    val am= AssetManager()
    /** 需要 Skin 主要是为了用 skin.newDrawable("white", tint) 生成纯色 drawable */
    val skin=Skin()
    // 1×1 白色像素纹理，提供 tint 生成任意纯色背景/边框
    val whiteTexture: Texture by lazy {
        val pm = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pm.setColor(Color.WHITE)
        pm.fill()
        val tex = Texture(pm)
        pm.dispose()
        tex
    }

    /** 缺资源时的占位纹理（建议用显眼颜色，方便你在开发期发现问题） */
    val placeholderTexture: Texture by lazy {
        val pm = Pixmap(2, 2, Pixmap.Format.RGBA8888)
        pm.setColor(Color.MAGENTA)
        pm.fill()
        val tex = Texture(pm)
        pm.dispose()
        tex
    }

    /** 缺资源时的占位 drawable（UI 用） */
    val placeholderDrawable: Drawable by lazy {
        // 用 skin 的 white drawable tint 一下（更统一）
        // 前提：Skin 里有 "white" 这个 drawable（下面 Skin 结构会教你怎么放）
        if (skin.has("white", Drawable::class.java)) {
            skin.newDrawable("white", Color(1f, 0f, 1f, 1f))
        } else {
            TextureRegionDrawable(TextureRegion(placeholderTexture))
        }
    }

    /** 统一的“存在性判断” */
    fun existsInternal(path: String): Boolean = Gdx.files.internal(path).exists()

    /**
     * 获取 Texture：
     * - 文件不存在 -> placeholder
     * - 已加载 -> 直接 get
     * - 存在但未加载 -> 可选择：返回 placeholder 或者同步加载该单个资源
     *
     * 这里我默认：存在但未加载 -> 同步加载该资源（开发期方便）。
     * 如果你想严格异步加载，把 autoLoadIfMissing 改成 false。
     */
    fun texture(paths: String?, autoLoadIfMissing: Boolean = true): Texture {
       val path=paths?.trim()
        if(path==null) return placeholderTexture
        if (!existsInternal(path)) return placeholderTexture
        if (am.isLoaded(path, Texture::class.java)) return am.get(path, Texture::class.java)

        if (!autoLoadIfMissing) return placeholderTexture

        return try {
            am.load(path, Texture::class.java)
            am.finishLoadingAsset<Texture>(path) // 只阻塞加载这个资源
            am.get(path, Texture::class.java)
        } catch (e: Exception) {
            Gdx.app.error("GameAssets", "Failed to load texture: $path", e)
            placeholderTexture
        }
    }

    fun region(path: String, autoLoadIfMissing: Boolean = true): TextureRegion {
        return TextureRegion(texture(path, autoLoadIfMissing))
    }

    fun drawable(path: String, autoLoadIfMissing: Boolean = true): Drawable {
        val tex = texture(path, autoLoadIfMissing)
        // 如果 tex 是 placeholderTexture，也照样返回 drawable（达到“永不崩”）
        return TextureRegionDrawable(TextureRegion(tex))
    }

    /**
     * 纯色 drawable：用于 Table 背景、按钮底色等
     * 依赖 skin 里 "white" 这个 drawable（推荐做法）
     */
    fun solid(color: Color): Drawable {
        return if (skin.has("white", Drawable::class.java)) {
            skin.newDrawable("white", color)
        } else {
            // 没有 white 的情况下退化：用 1x1 白纹理自己做 drawable
            TextureRegionDrawable(TextureRegion(whiteTexture)).tint(color)
        }
    }

    /**
     * 手动释放你自己 new 出来的 Texture（AssetManager 管的不用你管）
     */
    fun disposeManual() {
        // 这两个是 lazy 可能没初始化，安全起见用 runCatching
        runCatching { whiteTexture.dispose() }
        runCatching { placeholderTexture.dispose() }
    }
}
