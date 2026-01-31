package io.github.lv.ui.component

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Scaling
import com.github.czyzby.autumn.annotation.Component
import com.github.czyzby.autumn.annotation.Initiate
import com.github.czyzby.autumn.annotation.Inject
import com.kotcrab.vis.ui.widget.VisImage
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import io.github.lv.ecs.pawn.component.PawnAppearanceComponent
import io.github.lv.ui.somethingElse.UiDrawables
import io.github.lv.ui.somethingElse.ColorDrawable

@Component
class RightPanel {
    val actor = VisTable()
    @Inject
    lateinit var unitPortrait: Image

    object pawnInfo {
        var name: String = "<UNK>"
        var health: Int = 0
        var age: Int = 0

    }

    @Initiate
    fun initialize() {
        // ===== 右侧面板 =====
        actor.top().right()
//        rightPanel.background = rightPanelBg
        actor.background = ColorDrawable(Color(255f, 255f, 0f, 0.6f)) // 半透明黑

        // 头像框
        val uiDrawables = UiDrawables()
        unitPortrait.setScaling(Scaling.fit)
        unitPortrait.drawable = uiDrawables.portraitImg // 可选

        // 小地图下方一排按钮（用 ImageButton 更像图标）
        fun iconButton(name: String): ImageButton {
            // 你用自己的图：ui_icon_xxx.png
            val up = uiDrawables.icon(name)
            val style = ImageButton.ImageButtonStyle().apply { imageUp = up }
            return ImageButton(style)
        }

        val iconRow = VisTable()
//        iconRow.add(iconButton("search")).size(26f).pad(2f)
//        iconRow.add(iconButton("shield")).size(26f).pad(2f)
//        iconRow.add(iconButton("home")).size(26f).pad(2f)
//        iconRow.add(iconButton("people")).size(26f).pad(2f)
//        iconRow.add(iconButton("grid")).size(26f).pad(2f)
//        iconRow.add(iconButton("gear")).size(26f).pad(2f)
        // 坐标/地形信息
        val infoLine1 = VisLabel("21,49")
        val infoLine2 = VisLabel("山岭 (山岭)")
        // 横条 banner（你图里那个夕阳条）
        val banner = VisImage(uiDrawables.sunsetImg)
        banner.setScaling(Scaling.stretch)

        // 属性文字
        val stats = VisTable()
//        stats.add(VisLabel("name: ${selectedUnit?.getComponent(PawnInformationComponent::class.java)?.name}")).left()
        stats.add(VisLabel("name: ${pawnInfo.name}")).left()
            .row()
        stats.add(VisLabel("health:${pawnInfo.health}")).left()
            .padTop(8f).row()
        stats.add(VisLabel("experience")).left().padTop(8f).row()
        stats.add(VisLabel("age")).left().padTop(8f).row()

        // 底部大按钮
//        val endTurnStyle = VisTextButton.VisTextButtonStyle().apply {
//            font = skin.getFont("default-font")
//            up = uiDrawables.bottomBtnUp
//            down = uiDrawables.bottomBtnDown
//            fontColor = Color.WHITE
//        }
        val endTurnBtn = VisTextButton("end")
        // 右侧面板拼装
        actor.add(unitPortrait).width(220f).height(180f).pad(8f).row()
        actor.add(iconRow).padLeft(8f).padRight(8f).left().row()
        actor.add(infoLine1).padLeft(10f).padTop(6f).left().row()
        actor.add(infoLine2).padLeft(10f).padTop(2f).left().row()
        actor.add(banner).width(220f).height(40f).pad(8f).row()
        actor.add(stats).padLeft(8f).padRight(8f).left().row()
        actor.add().expandY().row() // 中间留空，撑开到下面
        actor.add(endTurnBtn).width(220f).height(44f).pad(10f).bottom()
    }

    fun update(selectedUnit: Entity?) {
        if (selectedUnit == null) {
            unitPortrait.drawable = null
            return
        }
        val appearance: PawnAppearanceComponent = selectedUnit.getComponent(PawnAppearanceComponent::class.java)
        unitPortrait.drawable = TextureRegionDrawable(
            TextureRegion(appearance.unitSprite.texture)
        )
    }
}
