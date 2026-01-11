package io.github.lv.GdxProvider


import com.badlogic.gdx.graphics.OrthographicCamera
import com.github.czyzby.autumn.annotation.Provider
import com.github.czyzby.kiwi.util.gdx.asset.lazy.provider.ObjectProvider

@Provider
class CameraProvider : ObjectProvider<OrthographicCamera> {

    private val camera = OrthographicCamera()

    override fun provide(): OrthographicCamera = camera
}
