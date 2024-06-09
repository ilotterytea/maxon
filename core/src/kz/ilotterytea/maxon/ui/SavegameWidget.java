package kz.ilotterytea.maxon.ui;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import kz.ilotterytea.maxon.MaxonGame;
import kz.ilotterytea.maxon.player.Savegame;
import kz.ilotterytea.maxon.screens.game.GameScreen;
import kz.ilotterytea.maxon.screens.WelcomeScreen;
import kz.ilotterytea.maxon.utils.formatters.NumberFormatter;

public class SavegameWidget extends Table implements Disposable {
    private final Skin skin;
    private final Savegame savegame;
    private final Table dataTable, controlTable;
    private final TextureAtlas atlas;
    private final MaxonGame game;
    private final Stage stage;

    public SavegameWidget(final MaxonGame game, Skin skin, final Stage stage, Savegame savegame) {
        super();
        this.game = game;
        this.stage = stage;
        this.atlas = game.assetManager.get("sprites/gui/player_icons.atlas", TextureAtlas.class);

        this.skin = skin;
        this.savegame = savegame;

        this.dataTable = new Table(this.skin);
        this.dataTable.pad(16f);
        this.dataTable.setBackground("bg");

        super.add(this.dataTable).grow().padBottom(16f).row();

        this.controlTable = new Table();
        this.controlTable.align(Align.left);
        super.add(this.controlTable).growX();

        if (savegame.isNewlyCreated()) {
            createEmpty();
        } else {
            createWithSavegame();
        }
    }

    private void createEmpty() {
        Table body = new Table();

        Label label = new Label("New Game", skin);
        label.setAlignment(Align.center);
        body.add(label).grow().row();

        this.dataTable.add(body).grow().row();

        body.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                moveToNextScreen();
            }
        });
    }

    private void createWithSavegame() {
        // - - -  S A V E G A M E  D A T A  - - -
        // Header
        Table header = new Table();

        Label name = new Label(savegame.getName(), skin);
        header.add(name).grow();

        long minutes = savegame.getElapsedTime() / 1000 / 60;
        long seconds = savegame.getElapsedTime() / 1000 % 60;

        Label time = new Label(String.format("%s:%s", NumberFormatter.pad(minutes), NumberFormatter.pad(seconds)), skin);
        time.setAlignment(Align.right);
        header.add(time).grow().row();

        this.dataTable.add(header).grow().row();

        // Data
        Table data = new Table();
        data.align(Align.left);

        // Points
        Image pointsIcon = new Image(atlas.findRegion("points"));
        data.add(pointsIcon).size(32f, 32f).padRight(8f);

        Label points = new Label(NumberFormatter.format((long) savegame.getMoney()), skin);
        data.add(points).padRight(32f);

        // Unit
        int amount = 0;

        for (int a : savegame.getPurchasedPets().values()) {
            amount += a;
        }

        Image unitIcon = new Image(atlas.findRegion("pets"));
        data.add(unitIcon).size(32f, 32f).padRight(8f);

        Label unit = new Label(NumberFormatter.format(amount), skin);
        data.add(unit).padRight(32f);

        // Multiplier
        Image multiplierIcon = new Image(atlas.findRegion("multiplier"));
        data.add(multiplierIcon).size(32f, 32f).padRight(8f);

        Label multiplier = new Label(NumberFormatter.format((long) savegame.getMultiplier()), skin);
        data.add(multiplier);

        this.dataTable.add(data).grow();

        // - - -  C O N T R O L  - - -
        TextButton playButton = new TextButton(game.locale.TranslatableText("menu.continue"), skin);
        controlTable.add(playButton).padRight(16f).growX();

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                moveToNextScreen();
            }
        });

        TextButton resetButton = new TextButton(game.locale.TranslatableText("menu.reset"), skin);
        controlTable.add(resetButton);

        resetButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                controlTable.clear();
                dataTable.clear();
                savegame.delete();
                createEmpty();
            }
        });
    }

    private void moveToNextScreen() {
        Image bg = new Image(skin, "white_tile");
        bg.setFillParent(true);

        bg.addAction(
                Actions.sequence(
                        Actions.alpha(0.0f),
                        Actions.alpha(1.0f, 1.5f),
                        Actions.delay(0.5f),
                        new Action() {
                            @Override
                            public boolean act(float delta) {
                                Screen screen;

                                if (savegame.isNewlyCreated()) {
                                    screen = new WelcomeScreen();
                                } else {
                                    screen = new GameScreen();
                                }

                                game.setScreen(screen);
                                return true;
                            }
                        }
                )
        );
        stage.addActor(bg);
    }

    @Override
    public void dispose() {
        atlas.dispose();
    }
}
