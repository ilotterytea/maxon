package com.ilotterytea.maxoning.ui;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

public class SaveSlot extends Stack {
    public SaveSlot(
            Skin skin,
            NinePatch ninePatch,
            float x, float y,
            float width, float height,
            CharSequence text
    ) {
        Image img = new Image(ninePatch);
        TypingLabel label = new TypingLabel(text, skin);

        img.setPosition(x, y);
        img.setSize(width, height);

        label.setPosition(img.getWidth() / 2, img.getHeight() / 2);

        super.add(img);
        super.add(label);
    }
}
