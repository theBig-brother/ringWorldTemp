package io.github.lv.ui.component

import com.github.czyzby.autumn.annotation.Component
import com.github.czyzby.autumn.annotation.Initiate
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable

@Component
class ResRow {
    val actor = VisTable()

    @Initiate
    fun initialize() {
        // 资源条（随便放几个 label 做占位）
        actor.add(VisLabel("⚔ 1")).padRight(12f)
        actor.add(VisLabel("💰 0")).padRight(12f)
        actor.add(VisLabel("🏠 0/2")).padRight(12f)
        actor.add(VisLabel("👤 6")).padRight(12f)
        actor.add(VisLabel("⛏ -4(4)")).padRight(12f)
        actor.add(VisLabel("✚ 0")).padRight(12f)
        actor.add(VisLabel("📘 40%")).padRight(12f)
        actor.add(VisLabel("⏱ 17:17")).padRight(12f)
    }

    fun update() {}
}
