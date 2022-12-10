package com.ilotterytea.maxoning.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Version
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.ilotterytea.maxoning.MaxonConstants
import com.ilotterytea.maxoning.utils.I18N

/**
 * Debug information.
 * @since a_1.0
 * @author ilotterytea
 */
class DebugInfo(skin: Skin, locale: I18N) : Table() {
    private val i18n = locale
    private var c_fps: Label
    private var c_mem: Label

    init {
        val rt = Runtime.getRuntime()
        val usedmem = ((rt.totalMemory() - rt.freeMemory()) / 1024) / 1024
        val totalmem = (rt.totalMemory() / 1024) / 1024

        // Version info:
        val ver = Label(i18n.FormattedText("debug.version", MaxonConstants.GAME_VERSION, Version.VERSION, System.getProperty("java.version")), skin, "debug")
        ver.setAlignment(Align.left)
        this.add(ver).fillX().row()

        // Frames per second:
        c_fps = Label(i18n.FormattedText("debug.c_fps", Gdx.graphics.framesPerSecond.toString()), skin, "debug")
        c_fps.setAlignment(Align.left)
        this.add(c_fps).fillX().row()

        // Memory usage:
        c_mem = Label(i18n.FormattedText("debug.c_mem", usedmem.toString(), totalmem.toString()), skin, "debug")
        c_mem.setAlignment(Align.left)
        this.add(c_mem).fillX().row()

        this.align(Align.left)
        this.skin = skin
        this.background("tile_03")
    }

    override fun act(delta: Float) {
        val rt = Runtime.getRuntime()
        val usedmem = ((rt.totalMemory() - rt.freeMemory()) / 1024) / 1024
        val totalmem = (rt.totalMemory() / 1024) / 1024

        super.act(delta)
        c_fps.setText(i18n.FormattedText("debug.c_fps", Gdx.graphics.framesPerSecond.toString()))
        c_mem.setText(i18n.FormattedText("debug.c_mem", usedmem.toString(), totalmem.toString()))
    }
}