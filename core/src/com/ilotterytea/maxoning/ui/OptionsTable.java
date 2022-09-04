package com.ilotterytea.maxoning.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.ilotterytea.maxoning.MaxonConstants;
import com.ilotterytea.maxoning.MaxonGame;
import com.ilotterytea.maxoning.screens.SplashScreen;
import com.ilotterytea.maxoning.utils.I18N;

import java.util.ArrayList;
import java.util.Locale;

public class OptionsTable extends Table {
    public OptionsTable(
            final MaxonGame game,
            Skin skin,
            NinePatch buttonUp,
            NinePatch buttonDown,
            NinePatch buttonOver,
            final Music music,
            final Table menuTable,
            final Image bgImage,
            final Image brandLogo
            ) {
        super();
        
        Label optionsLabel = new Label(game.locale.TranslatableText("options.title"), skin);
        optionsLabel.setAlignment(Align.center);
        super.add(optionsLabel).fillX().pad(81f).row();

        Table lidlOptionsTable = new Table();

        // Music button:
        final NinepatchButton musicButton = new NinepatchButton(buttonUp, buttonDown, buttonOver, game.locale.FormattedText("options.music", (game.prefs.getBoolean("music", true)) ? "ON" : "OFF"), skin, "default");

        musicButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.prefs.putBoolean("music", !game.prefs.getBoolean("music", true));
                game.prefs.flush();

                if (game.prefs.getBoolean("music", true)) {
                    music.setVolume(1f);
                    music.setLooping(true);
                    music.play();
                } else {
                    music.stop();
                }

                musicButton.setText(game.locale.FormattedText("options.music",  (game.prefs.getBoolean("music", true)) ? "ON" : "OFF"));
            }
        });

        lidlOptionsTable.add(musicButton).size(512f, 81f).pad(10f).left();

        final NinepatchButton soundButton = new NinepatchButton(buttonUp, buttonDown, buttonOver, game.locale.FormattedText("options.sound", (game.prefs.getBoolean("sound", true)) ? "ON" : "OFF"), skin, "default");

        soundButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.prefs.putBoolean("sound", !game.prefs.getBoolean("sound", true));
                game.prefs.flush();

                soundButton.setText(game.locale.FormattedText("options.sound",  (game.prefs.getBoolean("sound", true)) ? "ON" : "OFF"));
            }
        });

        lidlOptionsTable.add(soundButton).size(512f, 81f).pad(10f).right().row();

        final NinepatchButton vsyncButton = new NinepatchButton(buttonUp, buttonDown, buttonOver, game.locale.FormattedText("options.vsync", (game.prefs.getBoolean("vsync", true)) ? "ON" : "OFF"), skin, "default");

        vsyncButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.prefs.putBoolean("vsync", !game.prefs.getBoolean("vsync", true));
                game.prefs.flush();

                if (game.prefs.getBoolean("vsync", true)) {
                    Gdx.graphics.setVSync(true);
                } else {
                    Gdx.graphics.setVSync(false);
                }

                vsyncButton.setText(game.locale.FormattedText("options.vsync",  (game.prefs.getBoolean("vsync", true)) ? "ON" : "OFF"));
            }
        });

        lidlOptionsTable.add(vsyncButton).size(512f, 81f).pad(10f).left();

        final NinepatchButton fullscreenButton = new NinepatchButton(buttonUp, buttonDown, buttonOver, game.locale.FormattedText("options.fullscreen", (game.prefs.getBoolean("fullscreen", false)) ? "ON" : "OFF"), skin, "default");

        fullscreenButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.prefs.putBoolean("fullscreen", !game.prefs.getBoolean("fullscreen", false));
                game.prefs.flush();

                if (game.prefs.getBoolean("fullscreen", false)) {
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                } else {
                    Gdx.graphics.setWindowedMode(game.prefs.getInteger("width", Gdx.graphics.getWidth()), game.prefs.getInteger("height", Gdx.graphics.getHeight()));
                }

                fullscreenButton.setText(game.locale.FormattedText("options.fullscreen", (game.prefs.getBoolean("fullscreen", false)) ? "ON" : "OFF"));
            }
        });

        lidlOptionsTable.add(fullscreenButton).size(512f, 81f).pad(10f).right().row();

        super.add(lidlOptionsTable).center().row();

        String[] fh4Locale = game.locale.getFileHandle().nameWithoutExtension().split("_");
        Locale locale = new Locale(fh4Locale[0], fh4Locale[1]);

        final NinepatchButton switchLangButton = new NinepatchButton(buttonUp, buttonDown, buttonOver, game.locale.FormattedText("options.language", locale.getDisplayLanguage(), locale.getDisplayCountry()), skin, "default");

        switchLangButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int index = 0;
                ArrayList<FileHandle> fhArray = new ArrayList<>();
                fhArray.add(MaxonConstants.FILE_RU_RU);
                fhArray.add(MaxonConstants.FILE_EN_US);

                if (fhArray.indexOf(game.locale.getFileHandle()) + 1 < fhArray.size()) {
                    index = fhArray.indexOf(game.locale.getFileHandle()) + 1;
                }

                FileHandle fhNext = fhArray.get(index);

                game.locale = new I18N(fhNext);
                game.prefs.putString("lang", fhNext.nameWithoutExtension());
                game.prefs.flush();

                String[] fh4Locale = fhNext.nameWithoutExtension().split("_");
                Locale locale = new Locale(fh4Locale[0], fh4Locale[1]);

                switchLangButton.setText(game.locale.FormattedText("options.language", locale.getDisplayLanguage(), locale.getDisplayCountry()));
                game.setScreen(new SplashScreen(game));
            }
        });

        super.add(switchLangButton).size(1024f, 81f).padTop(91f).center().row();

        final NinepatchButton optionsCloseButton = new NinepatchButton(buttonUp, buttonDown, buttonOver, game.locale.TranslatableText("options.close"), skin, "default");

        optionsCloseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                close(menuTable, bgImage, brandLogo);
            }
        });

        super.add(optionsCloseButton).size(1024f, 81f).pad(91f).center().row();

        super.setPosition(0, 0);
        super.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void close(Table menu, Image bg, Image logo) {
        super.clearActions();
        super.addAction(Actions.moveTo(Gdx.graphics.getWidth(), super.getY(), 0.75f, Interpolation.sine));

        menu.clearActions();
        menu.addAction(Actions.moveTo(0, menu.getY(), 0.75f, Interpolation.sine));

        bg.clearActions();
        bg.addAction(Actions.alpha(0.25f));

        logo.addAction(
                Actions.moveTo(logo.getX(), logo.getY() - 512f, 0.5f, Interpolation.sine)
        );
    }
}
