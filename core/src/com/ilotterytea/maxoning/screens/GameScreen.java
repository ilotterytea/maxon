package com.ilotterytea.maxoning.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.ilotterytea.maxoning.MaxonGame;
import com.ilotterytea.maxoning.anim.SpriteUtils;
import com.ilotterytea.maxoning.player.MaxonItem;
import com.ilotterytea.maxoning.player.MaxonItemRegister;
import com.ilotterytea.maxoning.player.MaxonPlayer;
import com.ilotterytea.maxoning.ui.*;
import com.ilotterytea.maxoning.utils.serialization.GameDataSystem;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

import java.io.IOException;
import java.util.ArrayList;

public class GameScreen implements Screen, InputProcessor {
    final MaxonGame game;

    MaxonPlayer player;

    Stage stage;
    Skin skin;

    Window shopWindow;
    Image blackBg, pointsBar;
    AnimatedImageButton maxon;
    AnimatedImage maxonCat;
    Label pointsLabel, infoLabel;
    NinePatch button;
    SupaIconButton shopButton;
    Texture bgTile, bgTileAlt;
    ScrollPane scroll;

    ArrayList<MaxonItem> items;

    boolean isShopping = true;

    ArrayList<ArrayList<Sprite>> bgTiles;

    public GameScreen(MaxonGame game) throws IOException, ClassNotFoundException {
        this.game = game;

        player = new MaxonPlayer();
        player.load(GameDataSystem.LoadData());

        button = new NinePatch(game.assetManager.get("sprites/ui/button.9.png", Texture.class), 36, 36, 36, 36);

        stage = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        skin = new Skin(Gdx.files.internal("main.skin"));

        shopWindow = new Window("Pet Shop", skin);

        items = new ArrayList<>();

        for (int id : player.purchasedItems) {
            items.add(MaxonItemRegister.get(id));
        }

        infoLabel = new DebugLabel(skin);

        pointsLabel = new Label(Math.round(player.points) + " S", skin, "default");
        pointsBar = new Image(button);

        blackBg = new Image(game.assetManager.get("sprites/black.png", Texture.class));

        shopButton = new SupaIconButton(button, "Pet Shop", skin);

        bgTile = game.assetManager.get("sprites/menu/tile_1.png", Texture.class);
        bgTileAlt = game.assetManager.get("sprites/menu/tile_2.png", Texture.class);

        // Generate the background:
        bgTiles = new ArrayList<>();

        for (int i = 0; i < Gdx.graphics.getHeight() / bgTile.getHeight() + 1; i++) {
            bgTiles.add(i, new ArrayList<Sprite>());

            for (int j = -1; j < Gdx.graphics.getWidth() / bgTile.getWidth(); j++) {
                Sprite spr = new Sprite();

                if ((j + i) % 2 == 0) {
                    spr.setTexture(bgTile);
                } else {
                    spr.setTexture(bgTileAlt);
                }

                spr.setSize(bgTile.getWidth(), bgTile.getHeight());

                spr.setPosition(bgTile.getWidth() * j, bgTile.getHeight() * i);
                bgTiles.get(i).add(spr);
            }
        }

        maxonCat = new AnimatedImage(
                SpriteUtils.splitToTextureRegions(game.assetManager.get("sprites/sheet/loadingCircle.png", Texture.class),
                        112, 112, 10, 5
                )
        );

        maxonCat.disableAnim();

        maxon = new AnimatedImageButton(maxonCat);

        maxon.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                maxonCat.nextFrame();
                maxon.setDrawable(maxonCat.getDrawable());
                player.points += 1 * player.multiplier;

                final TypingLabel label = new TypingLabel("{SHAKE}{RAINBOW}+" + Math.round(1 * player.multiplier) + "{ENDRAINBOW}{ENDSHAKE}", skin, "default");

                label.setPosition(
                        maxon.getX() + (maxon.getWidth() / 2f) - 8,
                        maxon.getY() + maxon.getHeight()
                );

                label.addAction(Actions.parallel(
                        Actions.fadeOut(5f),
                        Actions.moveTo(
                                label.getX(), label.getY() + (float) Math.floor(Math.random() * 156), 5f, Interpolation.exp5Out)
                ));

                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        label.remove();
                    }
                }, 10f);

                stage.addActor(label);
            }
        });

        shopButton.setPosition(16f, 16f);

        maxon.setSize(maxonCat.getWidth() * 2f, maxonCat.getHeight() * 2f);

        maxon.setPosition(
                (Gdx.graphics.getWidth() / 2f) - (maxon.getWidth() / 2f),
                (Gdx.graphics.getHeight() / 2f) - (maxon.getHeight() / 2f)
        );

        infoLabel.setPosition(8, (Gdx.graphics.getHeight() - infoLabel.getHeight() - 8));

        pointsBar.setScale(1.75f, 1f);

        pointsBar.setPosition(
                (Gdx.graphics.getWidth() / 2.0f) - (pointsBar.getWidth() * 1.75f / 2.0f),
                Gdx.graphics.getHeight() - (pointsBar.getHeight() / 2f)
        );

        pointsLabel.setPosition(0, Gdx.graphics.getHeight() - pointsLabel.getHeight() - 8);
        pointsLabel.setWidth(Gdx.graphics.getWidth());
        pointsLabel.setAlignment(Align.center);

        blackBg.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        blackBg.addAction(Actions.parallel(Actions.alpha(0.25f)));

        Table shoptable = new Table();

        for (final MaxonItem item : MaxonItemRegister.getItems()) {
            item.icon.setSize(64, 64);
            PurchaseItem pitem = new PurchaseItem(skin, button, item.icon, item.name, item.desc, item.price);

            pitem.setSize(512, 512);

            pitem.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (item.price < player.points) {
                        player.purchasedItems.add(item.id);
                        player.points -= item.price;
                        player.multiplier += item.multiplier;
                    }
                }
            });

            shoptable.add(pitem).pad(16);
        }

        scroll = new ScrollPane(shoptable);

        scroll.setPosition(512f, -922f);
        scroll.setSize(Gdx.graphics.getWidth() - 512f, 256f);

        shopButton.setSize(256f, 90f);

        shopButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isShopping) {
                    scroll.clearActions();
                    scroll.addAction(Actions.moveTo(512f, 0f, 1f, Interpolation.sine));
                } else {
                    scroll.clearActions();
                    scroll.addAction(Actions.moveTo(512f, -922f, 5f, Interpolation.sine));
                }
                isShopping = !isShopping;
            }
        });

        stage.addActor(blackBg);
        stage.addActor(scroll);
        stage.addActor(shopButton);
        stage.addActor(maxon);
        //stage.addActor(pointsBar);
        stage.addActor(pointsLabel);
        stage.addActor(infoLabel);
        Gdx.input.setInputProcessor(new InputMultiplexer(this, stage));
    }

    @Override
    public void show() {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                float itemMultiplier = 0f;

                for (MaxonItem item : items) {
                    itemMultiplier += item.multiplier;
                }

                if (itemMultiplier > 0f) {
                    maxonCat.nextFrame();
                    maxon.setDrawable(maxonCat.getDrawable());

                    player.points += 1 * itemMultiplier;

                    TypingLabel label = new TypingLabel("{SHAKE}{RAINBOW}+" + Math.round(1 * itemMultiplier)  + "{ENDRAINBOW}{ENDSHAKE}", skin, "default");

                    label.setPosition(
                            maxon.getX() + (maxon.getWidth() / 2f) - 8,
                            maxon.getY() + maxon.getHeight()
                    );

                    label.addAction(Actions.parallel(
                            Actions.fadeOut(5f),
                            Actions.moveTo(
                                    label.getX(), label.getY() + (float) Math.floor(Math.random() * 156), 5f, Interpolation.exp5Out)
                    ));

                    stage.addActor(label);
                }
            }
        }, 5, 10);
        render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();

        for (ArrayList<Sprite> array : bgTiles) {
            for (Sprite spr : array) {
                spr.setPosition(spr.getX() + 1, spr.getY());
                spr.draw(game.batch);
            }
        }

        game.batch.end();

        for (ArrayList<Sprite> array : bgTiles) {
            for (int i = 0; i < array.size(); i++) {
                Sprite spr = array.get(i);
                Sprite f_spr = array.get(0);

                if (spr.getX() > Gdx.graphics.getWidth()) {
                    Sprite n_spr = spr;
                    n_spr.setPosition(f_spr.getX() - spr.getWidth(), f_spr.getY());

                    if (spr.getTexture() == f_spr.getTexture()) {
                        n_spr.setTexture(array.get(1).getTexture());
                    }

                    array.remove(spr);
                    array.add(0, n_spr);
                }
            }
        }

        pointsLabel.setText(Math.round(player.points) + " S (x" + player.multiplier + ")");

        stage.draw();
        stage.act(delta);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}

    @Override public void resume() {}

    @Override public void hide() { dispose(); }

    @Override
    public void dispose() {
        stage.clear();
        skin.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            try {
                GameDataSystem.SaveData(player);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            game.setScreen(new MenuScreen(game));
            dispose();
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
