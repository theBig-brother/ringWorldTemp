package io.github.lv.ui.somethingElse

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.utils.Drawable

class UiDrawables() {
    val res = GameAssets

    // ====== 图片类（按钮皮肤等）======
    val bottomBtnUp: Drawable by lazy { res.drawable("ui/ui_bigbutton_up.png") }
    val bottomBtnDown: Drawable by lazy { res.drawable("ui/ui_bigbutton_down.png") }
    val bottomBtnOver: Drawable by lazy { res.drawable("ui/ui_bigbutton_over.png") }
    val topBarBg: Drawable by lazy { res.drawable("ui/ui_topbar_bg.png") }
    val rightPanelBg: Drawable by lazy { res.drawable("ui/ui_rightpanel_bg.png") }

    //    val minimapImg: Drawable by lazy { res.drawable("ui/ui_minimap.png") }
    val minimapImg: Drawable by lazy { res.drawable("images/ui/amla-default.png") }
//    val portraitImg: Drawable by lazy { res.drawable("ui/ui_portrait.png") }
    val portraitImg: Drawable by lazy { res.drawable("images/ui/amla-default.png") }
    val sunsetImg: Drawable by lazy { res.drawable("ui/ui_banner.png") }

    // ====== 纯色类（面板背景/遮罩/边框）======
    val panelBg: Drawable by lazy { res.solid(Color(0f, 0f, 0f, 0.6f)) }          // 半透明黑
    val panelBgStrong: Drawable by lazy { res.solid(Color(0.08f, 0.08f, 0.08f, 0.85f)) }
    val borderGold: Drawable by lazy { res.solid(Color(0.9f, 0.7f, 0.2f, 1f)) }

    // 你也可以做方法：按需生成
    fun panel(alpha: Float = 0.6f): Drawable = res.solid(Color(0f, 0f, 0f, alpha))

    fun icon(name: String) = res.drawable("ui/ui_icon_$name.png")
}
