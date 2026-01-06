package io.github.lv.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

class Assets(private val am: AssetManager, private val skin: Skin) {

    private val placeholderDrawable: Drawable by lazy {
        skin.newDrawable("white", Color.DARK_GRAY)
    }

    fun drawable(path: String): Drawable {
        // 文件不存在 → 占位
        if (!Gdx.files.internal(path).exists()) return placeholderDrawable

        // 没加载 → 加载（或你也可以选择直接占位）
        if (!am.isLoaded(path, Texture::class.java)) {
            am.load(path, Texture::class.java)
            am.finishLoadingAsset<Texture>(path)
        }

        val tex = am.get(path, Texture::class.java)
        return TextureRegionDrawable(TextureRegion(tex))
    }
}
