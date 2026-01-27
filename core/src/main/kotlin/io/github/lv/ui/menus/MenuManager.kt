package io.github.lv.ui.menus

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.I18NBundle
import com.badlogic.gdx.utils.XmlReader
import com.github.czyzby.autumn.annotation.Component
import com.github.czyzby.autumn.annotation.Inject
import io.github.lv.Constant

@Component
class MenuManager {
    @Inject
    private lateinit var actionRegistry: ActionRegistry

    @Inject
    lateinit var architectMenu: ArchitectMenu

    @Inject
    lateinit var settingMenu: SettingMenu

    @Inject
    lateinit var otherMenu: OtherMenu

    companion object {
        val xmlReader = XmlReader()
        val bundle: I18NBundle = I18NBundle.createBundle(Gdx.files.internal("language/menu/menu_${Constant.languageCode}"))
        val uiXmlPath = "config/uiConfig/mapScreen/"
    }
}
