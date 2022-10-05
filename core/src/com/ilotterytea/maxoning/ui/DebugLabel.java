package com.ilotterytea.maxoning.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class DebugLabel extends Label {
    private static final String str_placeholder = "%s fps";

    public DebugLabel(Skin skin) {
        super(String.format(str_placeholder, Gdx.graphics.getFramesPerSecond()), skin, "debug");
        super.setColor(Color.LIME);
    }

    @Override public void act(float delta) {
        super.setText(String.format(str_placeholder, Gdx.graphics.getFramesPerSecond()));
    }
}
