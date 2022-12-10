package com.ilotterytea.maxoning.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.ilotterytea.maxoning.utils.I18N

/**
 * Debug information.
 * @since a_1.0
 * @author ilotterytea
 */
class DebugInfo(skin: Skin, locale: I18N) : Table() {
    private val i18n = locale
    private var fps: Label

    init {
        // Frames per second:
        fps = Label(i18n.FormattedText("debug.fps", Gdx.graphics.framesPerSecond.toString()), skin, "debug")
        this.add(fps).row()

        this.align(Align.top)
        this.height = 100f
        this.width = 100f
    }

    override fun act(delta: Float) {
        super.act(delta)
        fps.setText(i18n.FormattedText("debug.fps", Gdx.graphics.framesPerSecond.toString()))
    }
}