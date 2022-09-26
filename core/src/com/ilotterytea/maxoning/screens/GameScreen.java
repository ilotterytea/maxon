package com.ilotterytea.maxoning.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
import com.ilotterytea.maxoning.inputprocessors.CrossProcessor;
import com.ilotterytea.maxoning.player.MaxonItem;
import com.ilotterytea.maxoning.player.MaxonItemRegister;
import com.ilotterytea.maxoning.player.MaxonPlayer;
import com.ilotterytea.maxoning.ui.*;
import com.ilotterytea.maxoning.utils.serialization.GameDataSystem;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameScreen implements Screen, InputProcessor {
    final MaxonGame game;

    MaxonPlayer player;

    Stage stage;
    Skin skin, widgetSkin;

    TextureAtlas widgetAtlas, environmentAtlas;

    Label pointsLabel;
    Image blackBg, inventoryBg, shopBg, pointsBg;
    AnimatedImage cat;
    AnimatedImageButton maxon;

    Table petTable, inventoryTable, mainTable;
    ScrollPane petScroll;

    ArrayList<MaxonItem> items;
    Map<Integer, Integer> invItems;

    ArrayList<ArrayList<Sprite>> bgTiles;

    public GameScreen(MaxonGame game) throws IOException, ClassNotFoundException {
        this.game = game;

        player = new MaxonPlayer();
        player.load(GameDataSystem.LoadData());

        // Initializing the stage and skin:
        stage = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        skin = new Skin(Gdx.files.internal("main.skin"));
        widgetSkin = new Skin(Gdx.files.internal("sprites/gui/widgets.skin"));

        widgetAtlas = game.assetManager.get("sprites/gui/widgets.atlas", TextureAtlas.class);
        environmentAtlas = game.assetManager.get("sprites/env/environment.atlas", TextureAtlas.class);

        items = new ArrayList<>();

        for (int id : player.purchasedItems) {
            items.add(MaxonItemRegister.get(id));
        }

        // Make the background a little dimmed:
        blackBg = new Image();
        blackBg.setColor(0f, 0f, 0f, 1f);
        blackBg.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        blackBg.addAction(Actions.parallel(Actions.alpha(0.25f)));
        stage.addActor(blackBg);

        // Setting the background for inventory:
        inventoryBg = new Image(widgetSkin, "button_disabled");
        inventoryBg.setSize((Gdx.graphics.getWidth() / 2.0f) - 512f, (Gdx.graphics.getHeight() / 2.0f) - 8f);
        inventoryBg.setPosition(8, 4);
        stage.addActor(inventoryBg);

        // Setting the title for inventory "window":
        Label inventoryLabel = new Label(game.locale.TranslatableText("game.inventory.title"), skin);
        inventoryLabel.setWidth(inventoryBg.getWidth());
        inventoryLabel.setPosition(inventoryBg.getX(), inventoryBg.getY() + inventoryBg.getHeight() - inventoryLabel.getHeight());
        inventoryLabel.setAlignment(Align.center);
        stage.addActor(inventoryLabel);

        // Inventory:
        inventoryTable = new Table();
        inventoryTable.setSize(inventoryBg.getWidth(), inventoryBg.getHeight() - inventoryLabel.getHeight());
        inventoryTable.setPosition(inventoryBg.getX(), inventoryBg.getY());

        invItems = new HashMap<>();

        for (Integer id : player.purchasedItems) {
            if (invItems.containsKey(id)) {
                invItems.put(id, invItems.get(id) + 1);
            } else {
                invItems.put(id, 1);
            }
        }

        // Put the items in the inventory table:
        for (Integer id : invItems.keySet()) {
            MaxonItem item = MaxonItemRegister.get(id);

            if (item != null) {
                InventoryAnimatedItem invItem = new InventoryAnimatedItem(item, skin, invItems.get(id));
                inventoryTable.add(invItem).size(64, 64).pad(5f);
            }
        }

        inventoryTable.align(Align.left|Align.top);

        stage.addActor(inventoryTable);

        // Setting the background for pet shop:
        shopBg = new Image(widgetSkin, "button_disabled");
        shopBg.setSize((Gdx.graphics.getWidth() / 2.0f) - 512f, (Gdx.graphics.getHeight() / 2.0f) - 8f);
        shopBg.setPosition(8, inventoryBg.getY() + inventoryBg.getHeight() + 8f);
        stage.addActor(shopBg);

        // Setting the title for pet shop "window":
        Label petshopLabel = new Label(game.locale.TranslatableText("game.petShop"), skin);
        petshopLabel.setWidth(shopBg.getWidth());
        petshopLabel.setPosition(shopBg.getX(), shopBg.getY() + shopBg.getHeight() - petshopLabel.getHeight());
        petshopLabel.setAlignment(Align.center);
        stage.addActor(petshopLabel);

        // Table for pets:
        petTable = new Table();

        // Adding the pet items in pet table:
        for (final MaxonItem item : MaxonItemRegister.getItems()) {
            PurchaseItem purchaseItem = new PurchaseItem(
                    skin, widgetSkin, item.icon, item.name, item.desc, item.price
            );

            purchaseItem.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (player.points > item.price) {
                        player.points -= item.price;
                        player.multiplier += item.multiplier;
                        player.purchasedItems.add(item.id);
                        items.add(item);

                        invItems.clear();
                        inventoryTable.clear();

                        for (Integer id : player.purchasedItems) {
                            if (invItems.containsKey(id)) {
                                invItems.put(id, invItems.get(id) + 1);
                            } else {
                                invItems.put(id, 1);
                            }
                        }

                        // Put the items in the inventory table:
                        for (Integer id : invItems.keySet()) {
                            MaxonItem item = MaxonItemRegister.get(id);

                            if (item != null) {
                                InventoryAnimatedItem invItem = new InventoryAnimatedItem(item, skin, invItems.get(id));
                                inventoryTable.add(invItem).size(64, 64).pad(5f);
                            }
                        }
                    }
                }
            });

            petTable.add(purchaseItem).width(shopBg.getWidth() - 12f).minHeight(128f).maxHeight(256f).padBottom(5f).row();
        }

        petTable.align(Align.center);

        // Scroll panel for pet shop table:
        petScroll = new ScrollPane(petTable);
        petScroll.setPosition(shopBg.getX() + 4f, shopBg.getY() + 4f);
        petScroll.setSize(shopBg.getWidth() - 8f, shopBg.getHeight() - petshopLabel.getHeight() - 8f);

        stage.addActor(petScroll);

        // Background for points label:
        pointsBg = new Image(widgetSkin, "button_disabled");
        pointsBg.setSize((Gdx.graphics.getWidth() - (shopBg.getX() + shopBg.getWidth()) - 8f), 64f);
        pointsBg.setPosition(shopBg.getX() + shopBg.getWidth() + 4f, Gdx.graphics.getHeight() - pointsBg.getHeight() - 4f);

        stage.addActor(pointsBg);

        // Points label:
        pointsLabel = new Label(game.locale.FormattedText("game.points",
                String.valueOf(player.points),
                String.valueOf(player.multiplier)
        ), skin);

        pointsLabel.setPosition(pointsBg.getX(), pointsBg.getY());
        pointsLabel.setSize(pointsBg.getWidth(), pointsBg.getHeight());
        pointsLabel.setAlignment(Align.center);

        stage.addActor(pointsLabel);

        // Generate the background:
        bgTiles = new ArrayList<>();

        genNewBgTiles((int) stage.getWidth(), (int) stage.getHeight());

        // Table for Maxon cat:
        mainTable = new Table();
        mainTable.setPosition(inventoryBg.getX() + inventoryBg.getWidth() + 4f, inventoryBg.getY());
        mainTable.setSize(Gdx.graphics.getWidth() - (inventoryBg.getX() + inventoryBg.getWidth()) - 8f, Gdx.graphics.getHeight() - pointsBg.getHeight() - 8f);

        // Creating the Maxon cat:
        cat = new AnimatedImage(
                SpriteUtils.splitToTextureRegions(game.assetManager.get("sprites/sheet/loadingCircle.png", Texture.class),
                        112, 112, 10, 5
                )
        );
        cat.disableAnim(); // Disable the image animation.
        maxon = new AnimatedImageButton(cat); // Make button with animated image.

        maxon.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                displayPointIncrease();
            }
        });

        mainTable.add(maxon).size(cat.getWidth() * 2f, cat.getHeight() * 2f).center();
        stage.addActor(mainTable);

        DebugLabel debugLabel = new DebugLabel(skin);

        debugLabel.setPosition(
                8,
                (Gdx.graphics.getHeight() - debugLabel.getHeight()) - 8
        );

        stage.addActor(debugLabel);

        Gdx.input.setInputProcessor(new InputMultiplexer(this, new CrossProcessor(), stage));
    }

    @Override
    public void show() {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Cell<AnimatedImageButton> actor = mainTable.getCell(maxon);
                float multiplier = 0;

                for (MaxonItem item : items) {
                    multiplier += item.multiplier;
                }

                player.points += multiplier;

                final TypingLabel label = new TypingLabel(game.locale.FormattedText("game.newPoint", String.valueOf(1 * player.multiplier)), skin, "default");

                label.setPosition(
                        mainTable.getX() + actor.getActorX(),
                        mainTable.getY() + actor.getActorY() + actor.getActorHeight()
                );

                label.setWidth(actor.getActorWidth());

                label.setAlignment(Align.center);

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
        }, 5, 5);

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

        // Update the points label:
        pointsLabel.setText(game.locale.FormattedText("game.points",
                String.valueOf(player.points),
                String.valueOf(player.multiplier)
        ));

        stage.draw();
        stage.act(delta);
    }

    @Override
    public void resize(int width, int height) {
        bgTiles.clear();

        genNewBgTiles(width, height);

        stage.getViewport().update(width, height, true);
    }

    private void genNewBgTiles(int width, int height) {
        for (int i = 0; i < height / environmentAtlas.findRegion("tile").getRegionHeight() + 1; i++) {
            bgTiles.add(i, new ArrayList<Sprite>());
            for (int j = -1; j < width / environmentAtlas.findRegion("tile").getRegionWidth(); j++) {
                Sprite spr = new Sprite(environmentAtlas.findRegion("tile"));

                if ((j + i) % 2 == 0) {
                    spr.setColor(0.98f, 0.71f, 0.22f, 1f);
                } else {
                    spr.setColor(0.84f, 0.61f, 0.20f, 1f);
                }

                spr.setSize(64, 64);

                spr.setPosition(spr.getWidth() * j, spr.getHeight() * i);
                bgTiles.get(i).add(spr);
            }
        }
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
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            displayPointIncrease();
        }
        return false;
    }

    private void displayPointIncrease() {
        Cell<AnimatedImageButton> actor = mainTable.getCell(maxon);

        cat.nextFrame();
        maxon.setDrawable(cat.getDrawable());

        player.points += 1 * player.multiplier;

        final TypingLabel label = new TypingLabel(game.locale.FormattedText("game.newPoint", String.valueOf(1 * player.multiplier)), skin, "default");

        label.setPosition(
                mainTable.getX() + actor.getActorX(),
                mainTable.getY() + actor.getActorY() + actor.getActorHeight()
        );

        label.setWidth(actor.getActorWidth());

        label.setAlignment(Align.center);

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
