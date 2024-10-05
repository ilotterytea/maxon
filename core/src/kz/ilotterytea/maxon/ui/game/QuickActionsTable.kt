package kz.ilotterytea.maxon.ui.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import kz.ilotterytea.maxon.MaxonGame
import kz.ilotterytea.maxon.player.Savegame
import kz.ilotterytea.maxon.screens.MenuScreen
import kz.ilotterytea.maxon.ui.ShakingImageButton

class QuickActionsTable(skin: Skin) : Table() {
    init {
        val game = MaxonGame.getInstance()
        val quitButton = ShakingImageButton(skin, "exit")

        val clickSound = game.assetManager.get("sfx/ui/click.ogg", Sound::class.java)

        quitButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                super.clicked(event, x, y)
                clickSound.play()
                Savegame.getInstance().save()
                game.screen = MenuScreen()
            }
        })

        add(quitButton).height(64f).width(64f)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)

        // i'm not sure how much does it affect on performance
        setX(Gdx.graphics.width - 36f, Align.left)
        setY(Gdx.graphics.height - 36f, Align.top)
    }
}