package kz.ilotterytea.maxon.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener

class ShakingImageButton(skin: Skin, style: String) : Image(skin.getRegion(style)) {
    init {
        setOrigin(width / 2f, height / 2f)

        addListener(object : ClickListener() {
            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                super.enter(event, x, y, pointer, fromActor)

                addAction(
                    Actions.repeat(
                        RepeatAction.FOREVER,
                        Actions.sequence(
                            Actions.rotateTo(-2f, 0.1f),
                            Actions.rotateTo(2f, 0.1f)
                        )
                    )
                )
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                super.exit(event, x, y, pointer, toActor)
                clearActions()
                addAction(Actions.rotateTo(0f, 0.1f))
            }
        })
    }
}