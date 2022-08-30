package com.ilotterytea.maxoning.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.ilotterytea.maxoning.MaxonConstants;

public class DebugLabel extends Label {
    private static final String str_placeholder = "%s\n%s fps";

    public DebugLabel(Skin skin) {
        super(String.format(str_placeholder, MaxonConstants.GAME_VERSION, Gdx.graphics.getFramesPerSecond()), skin);
    }

    @Override public void act(float delta) {
        super.setText(String.format(str_placeholder, MaxonConstants.GAME_VERSION, Gdx.graphics.getFramesPerSecond()));
    }
}
