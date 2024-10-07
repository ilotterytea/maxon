package kz.ilotterytea.maxon.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Screen
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Timer
import com.badlogic.gdx.utils.Timer.Task
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import kz.ilotterytea.maxon.MaxonGame
import kz.ilotterytea.maxon.player.Savegame
import kz.ilotterytea.maxon.screens.game.GameScreen
import kz.ilotterytea.maxon.utils.formatters.NumberFormatter
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.random.Random

private enum class Slot(val multiplier: Int) {
    Arbuz(5),
    Icecream(30),
    Kochan(80),
    Buter(120),
    Corn(200),
    Kebab(500),
    Onions(1000),
    Treat(2500),
}

private class SlotImage(slot: Slot, assetManager: AssetManager) : Image(assetManager.get("sprites/minigames/slots/${slot.name.lowercase()}.png", Texture::class.java))

class SlotsMinigameScreen : Screen {
    private val savegame = Savegame.getInstance()

    private val game = MaxonGame.getInstance()
    private val stage = Stage(FitViewport(800f, 600f))

    private var spinButton: TextButton? = null
    private var exitButton: TextButton? = null

    private var prize = 0.0
    private var prizeLabel: Label? = null
    private var moneyLabel: Label? = null

    private var stake = 0.0
    private var stakeField: TextField? = null

    private var loseSlot = Slot.values()[0]
    private var loseStreak = 0
    private var maxLoseStreak = Random.nextInt(20, 50)

    private var finished = false
    private var disabled = false
    private var lockedColumns = -1
    private val columnSlots = arrayListOf<Slot>()
    private val columns = Table()

    private val tasks = arrayListOf<Pair<Task, Float>>()

    private val audioLoop: Music = game.assetManager.get("mus/minigames/slots/slots_loop.mp3")

    override fun show() {
        // Skins
        val skin = game.assetManager.get("sprites/gui/ui.skin", Skin::class.java)

        // Main table
        val table = Table()
        table.setFillParent(true)
        table.align(Align.center)

        stage.addActor(table)

        // Background
        val background = Image(game.assetManager.get("sprites/minigames/slots/background.png", Texture::class.java))
        background.zIndex = 2

        table.add(background)

        // Buttons
        spinButton = TextButton(game.locale.TranslatableText("minigame.slots.spin_button"), skin)
        spinButton?.isDisabled = true
        spinButton?.width = 420f
        spinButton?.setPosition(62f, 60f)
        spinButton?.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (spinButton?.isDisabled == true) return
                super.clicked(event, x, y)
                restart()
            }
        })
        stage.addActor(spinButton)

        exitButton = TextButton(game.locale.TranslatableText("minigame.slots.exit_button"), skin)
        exitButton?.setPosition(62f, stage.height / 2f - 150f)
        exitButton?.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (exitButton?.isDisabled == true) return
                super.clicked(event, x, y)
                game.screen = GameScreen()
            }
        })

        stage.addActor(exitButton)

        // Labels
        prizeLabel = Label("", skin, "slots")
        prizeLabel?.setAlignment(Align.center)
        prizeLabel?.setPosition(stage.width / 2f - 180f, stage.height / 2f + 80f)
        stage.addActor(prizeLabel)

        val moneyIcon = Image(game.assetManager.get("sprites/gui/player_icons.atlas", TextureAtlas::class.java).findRegion("points"))
        moneyIcon.setSize(20f, 20f)
        moneyIcon.setPosition(stage.width / 2f + 60f, stage.height / 2f - 180f)
        stage.addActor(moneyIcon)

        moneyLabel = Label(NumberFormatter.format(savegame.money.toLong()), skin, "slots")
        moneyLabel?.setAlignment(Align.right)
        moneyLabel?.setPosition(stage.width / 2f, stage.height / 2f - 180f)
        stage.addActor(moneyLabel)

        val stakeLabel = Label(game.locale.TranslatableText("minigame.slots.bet"), skin, "slots")
        stakeLabel.setAlignment(Align.center)
        stakeLabel.setPosition(stage.width / 2f - 40f, stage.height / 2f - 100f)
        stage.addActor(stakeLabel)

        stakeField = TextField("", skin)
        stakeField?.messageText = "---"
        stakeField?.setTextFieldFilter { _, c -> c.toString().matches(Regex("^[0-9]*\$")) }
        stakeField?.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val textField = actor as TextField
                if (textField.isDisabled) return

                var value = textField.text?.toLongOrNull()

                if (value != null) {
                    if (value > savegame.money.toLong()) {
                        value = savegame.money.roundToLong()
                    }

                    textField.text = value.toString()
                    stake = value.toDouble()

                    spinButton?.isDisabled = stake <= 0.0
                }
            }
        })
        stakeField?.setPosition(stage.width / 2f - 70f, stage.height / 2f - 150f)

        stage.addActor(stakeField)

        // Slot columns
        columns.x = 62f
        columns.y = stage.height / 2f
        columns.width = 424f
        stage.addActor(columns)

        for (i in 0..2) {
            columnSlots.add(Slot.values()[Random.nextInt(0, Slot.values().size)])
        }

        reRoll()

        val updateTask = object : Task() {
            override fun run() {
                reRoll()
            }
        }

        tasks.add(Pair(updateTask, 0.1f))

        val lockColumnTask = object : Task() {
            override fun run() {
                val sound = game.assetManager.get("sfx/minigames/slots/slots_lock.ogg", Sound::class.java)
                sound.play()

                lockedColumns += 1

                if (lockedColumns > 1) {
                    finish()
                }
            }
        }

        tasks.add(Pair(lockColumnTask, 2f))

        disableSlotMachineIfNoStake()

        audioLoop.isLooping = true

        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)

        stage.act(delta)
        stage.draw()
    }

    private fun reRoll() {
        val array = arrayListOf<Slot>()

        for (i in 0 until columnSlots.size) {
            while (true) {
                var x = columnSlots[i]

                if (i <= lockedColumns) {
                    if (loseStreak >= maxLoseStreak) {
                        x = loseSlot
                    }

                    array.add(x)
                    break
                }

                val slot = Slot.values()[Random.nextInt(0, Slot.values().size)]

                if (x.ordinal != slot.ordinal) {
                    array.add(slot)
                    break
                }
            }
        }

        columnSlots.clear()
        columns.clear()

        for (x in array) {
            columnSlots.add(x)
            columns.add(SlotImage(x, game.assetManager))
                .size(100f, 100f)
                .expandX()
        }
    }


    private fun finish() {
        if (audioLoop.isPlaying) audioLoop.stop()

        finished = true
        stakeField?.isDisabled = false
        exitButton?.isDisabled = false
        spinButton?.isDisabled = false

        for (x in tasks) {
            x.first.cancel()
        }

        giveReward()
        updateLabels()
        disableSlotMachineIfNoStake()
    }

    private fun restart() {
        if (game.prefs.getBoolean("music", true)) audioLoop.play()

        val sound = game.assetManager.get<Sound>("sfx/minigames/slots/slots_start.ogg")
        sound.play()

        prizeLabel?.setText("")

        exitButton?.isDisabled = true
        spinButton?.isDisabled = true
        stakeField?.isDisabled = true

        loseSlot = Slot.values()[Random.nextInt(0,3)]
        finished = false
        lockedColumns = -1
        loseStreak = 0
        prize = 0.0
        maxLoseStreak = Random.nextInt(20, 50)

        reRoll()

        for (task in tasks) {
            Timer.schedule(task.first, task.second, task.second)
        }
    }

    private fun giveReward() {
        val first = columnSlots[0]
        var same = false

        for (x in columnSlots) {
            same = x.ordinal == first.ordinal

            if (!same) {
                break
            }
        }

        playRewardSound(same, first)

        if (!same) {
            loseStreak++
            savegame.money -= stake
            return
        }

        prize = stake * first.multiplier
        savegame.money += prize
    }

    private fun updateLabels() {
        val prizeText = if (prize == 0.0) {
            game.locale.TranslatableText("minigame.slots.nothing")
        } else {
            game.locale.FormattedText("minigame.slots.prize", NumberFormatter.format(prize.toLong()))
        }

        prizeLabel?.setText(prizeText)

        if (stake.toLong() > savegame.money.toLong()) {
            stake = savegame.money

            val stakeText = if (savegame.money.roundToInt() <= 0) {
                "---"
            } else {
                NumberFormatter.format(savegame.money.toLong())
            }

            stakeField?.text = stakeText
        }

        moneyLabel?.setText(NumberFormatter.format(savegame.money.roundToLong()))
    }

    private fun disableSlotMachineIfNoStake() {
        if (savegame.money.toLong() > 0) {
            return
        }

        disabled = true

        stakeField?.messageText = "---"
        stakeField?.text = "---"
        stakeField?.isDisabled = disabled
        spinButton?.isDisabled = disabled

        for (x in tasks) {
            x.first.cancel()
        }
    }

    private fun playRewardSound(same: Boolean, slot: Slot) {
        val path = if (!same) {
            "fail"
        } else {
            when (slot.ordinal) {
                in 0..1 -> "small_win"
                in 2..4 -> "medium_win"
                else -> "big_win"
            }
        }

        val sound = game.assetManager.get("sfx/minigames/slots/slots_$path.ogg", Sound::class.java)
        sound.play()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun pause() {
        dispose()
    }

    override fun resume() {
        show()
    }

    override fun hide() {
        dispose()
    }

    override fun dispose() {
        for (x in tasks) {
            x.first.cancel()
        }

        tasks.clear()
        stage.dispose()
        audioLoop.stop()
    }
}