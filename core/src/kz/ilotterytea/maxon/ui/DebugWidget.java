package kz.ilotterytea.maxon.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import kz.ilotterytea.maxon.MaxonConstants;

public class DebugWidget extends Table {
    private final Label fpsLabel, versionLabel;
    private final Preferences preferences;

    private boolean isEnabled;

    public DebugWidget(Skin skin) {
        super();
        super.pad(16f);
        super.setFillParent(true);
        super.align(Align.topRight);
        super.setZIndex(10);

        this.preferences = Gdx.app.getPreferences(MaxonConstants.GAME_APP_PACKAGE);
        this.isEnabled = preferences.getBoolean("debug", false);

        this.fpsLabel = new Label(Gdx.graphics.getFramesPerSecond() + " fps", skin);
        this.versionLabel = new Label(MaxonConstants.GAME_NAME + " " + MaxonConstants.GAME_VERSION, skin);

        if (isEnabled) {
            super.add(versionLabel).right().row();
            super.add(fpsLabel).right();
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            isEnabled = !isEnabled;
            this.preferences.putBoolean("debug", isEnabled);
            this.preferences.flush();

            if (isEnabled) {
                super.add(versionLabel).right().row();
                super.add(fpsLabel).right();
            } else {
                super.clear();
            }
        }

        if (!isEnabled) {
            return;
        }

        fpsLabel.setText(Gdx.graphics.getFramesPerSecond() + " fps");
    }
}
