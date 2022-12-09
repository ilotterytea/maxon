package com.ilotterytea.maxoning.ui

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.ilotterytea.maxoning.MaxonGame
import com.ilotterytea.maxoning.player.MaxonSavegame
import com.ilotterytea.maxoning.screens.GameScreen
import com.ilotterytea.maxoning.utils.formatters.NumberFormatter

/**
 * Savegame information widget.
 * @since 1.3
 * @author ilotterytea
 */
class SavegameInfo(
    game: MaxonGame,
    skin: Skin,
    sav: MaxonSavegame?,
    savId: Int
) : Table(skin) {
    init {
        this.setBackground("bg")
        this.width = 512f
        this.height = 324f
        this.align(Align.top)

        val title = Label(if (sav != null) "Save ${savId + 1} (${sav.name})" else "New Game", skin, "with_background")
        this.add(title).width(506f).pad(6f).row()

        val content = Table()
        content.align(Align.top)
        this.add(content).width(506f).maxWidth(506f).pad(6f).expandY().row()

        // - - -  A C T I O N S  - - - :
        val actions = Table()
        this.add(actions).width(508f).row()

        if (sav != null) {
            // - - -  P O I N T S  - - - :
            // Label for points:
            val pointsLabel = Label("Points", skin)
            content.add(pointsLabel).width(246f).pad(4f)
            // Label for points count:
            val pointsCLabel = Label(NumberFormatter.format(sav.points.toLong()), skin)
            pointsCLabel.setAlignment(Align.right)
            content.add(pointsCLabel).width(246f).pad(4f).row()

            // - - -  M U L T I P L I E R  - - - :
            // Label for multiplier:
            val mpLabel = Label("Multiplier", skin)
            content.add(mpLabel).width(246f).pad(4f)
            // Label for multiplier count:
            val mpCLabel = Label("+${NumberFormatter.format(sav.multiplier.toLong())}/click", skin)
            mpCLabel.setAlignment(Align.right)
            content.add(mpCLabel).width(246f).pad(4f).row()

            // - - -   P U R C H A S E D  I T E M S  - - - :
            // Label for purchased items:
            val piLabel = Label("Inventory size", skin)
            content.add(piLabel).width(246f).pad(4f)
            // Label for purchased items count:
            val piCLabel = Label(sav.inv.size.toString(), skin)
            piCLabel.setAlignment(Align.right)
            content.add(piCLabel).width(246f).pad(4f).row()

            // Deletion button:
            val delButton = ImageButton(skin, "delete")

            delButton.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    super.clicked(event, x, y)
                }
            })

            actions.add(delButton).pad(4f)

            // Play button:
            val playButton = TextButton("Play!", skin)

            playButton.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent, x: Float, y: Float) {
                    game.screen = GameScreen(game, sav, savId)
                }
            })

            actions.add(playButton).width(444f)
        } else {
            // - - -  N A M E  - - - :
            // Label for points:
            val nameLabel = Label("Your name", skin)
            content.add(nameLabel).width(246f).pad(4f)
            // Label for points count:
            val nameField = TextField(System.getProperty("user.name"), skin)
            content.add(nameField).width(246f).pad(4f).row()


            // Play button:
            val playButton = TextButton("START!", skin)

            playButton.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent, x: Float, y: Float) {
                    val _sav = MaxonSavegame()
                    _sav.name = nameField.text

                    game.screen = GameScreen(game, _sav, savId)
                }
            })

            actions.add(playButton).width(502f)
        }


    }
}