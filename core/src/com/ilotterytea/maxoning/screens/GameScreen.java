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
import com.ilotterytea.maxoning.MaxonConstants;
import com.ilotterytea.maxoning.MaxonGame;
import com.ilotterytea.maxoning.anim.SpriteUtils;
import com.ilotterytea.maxoning.inputprocessors.CrossProcessor;
import com.ilotterytea.maxoning.player.MaxonItem;
import com.ilotterytea.maxoning.player.MaxonItemRegister;
import com.ilotterytea.maxoning.player.MaxonSavegame;
import com.ilotterytea.maxoning.ui.*;
import com.ilotterytea.maxoning.utils.math.Math;
import com.ilotterytea.maxoning.utils.serialization.GameDataSystem;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameScreen implements Screen, InputProcessor {
    final MaxonGame game;
    final int slotId;
    final long playTimestamp;
    boolean isShopping = false, isInventoryEnabled = false;

    MaxonSavegame player;

    Stage stage;
    Skin skin, widgetSkin;

    TextureAtlas widgetAtlas, environmentAtlas;

    Label pointsLabel, multiplierLabel;
    Image blackBg, inventoryBg, shopBg, pointsBg;
    AnimatedImage cat;
    AnimatedImageButton maxon;

    Table boardTable, quickTable;
    ScrollPane petScroll;

    Dialog notEnoughPointsDialog;

    ArrayList<MaxonItem> items;
    Map<Integer, Integer> invItems;

    ArrayList<ArrayList<Sprite>> bgTiles;

    public GameScreen(MaxonGame game, MaxonSavegame sav, int slotId) throws IOException, ClassNotFoundException {
        this.game = game;
        this.slotId = slotId;
        this.playTimestamp = System.currentTimeMillis();

        player = sav;

        // Initializing the stage and skin:
        stage = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        skin = new Skin(Gdx.files.internal("main.skin"));
        widgetSkin = new Skin(Gdx.files.internal("sprites/gui/widgets.skin"));

        widgetAtlas = game.assetManager.get("sprites/gui/widgets.atlas", TextureAtlas.class);
        environmentAtlas = game.assetManager.get("sprites/env/environment.atlas", TextureAtlas.class);

        items = new ArrayList<>();

        for (int id : player.inv) {
            items.add(MaxonItemRegister.get(id));
        }

        invItems = new HashMap<>();

        for (Integer id : player.inv) {
            if (invItems.containsKey(id)) {
                invItems.put(id, invItems.get(id) + 1);
            } else {
                invItems.put(id, 1);
            }
        }

        // Make the background a little darker:
        blackBg = new Image(environmentAtlas.findRegion("tile"));
        blackBg.setColor(0f, 0f, 0f, 0.5f);
        blackBg.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(blackBg);

        // - - - - - -  I N F O  B O A R D  - - - - - - :
        boardTable = new Table(widgetSkin);
        boardTable.setBackground("board_bg");
        boardTable.setSize(stage.getWidth(), 86f);
        boardTable.setPosition(0, stage.getHeight() - boardTable.getHeight());
        boardTable.align(Align.left | Align.center);

        stage.addActor(boardTable);

        // - - -  P O I N T S  - - - :
        // Icon for points label:
        Image pointsIcon = new Image(widgetAtlas.findRegion("coin"));
        boardTable.add(pointsIcon).size(24f).padLeft(6f).padRight(6f);

        // Label for points:
        pointsLabel = new Label(MaxonConstants.DECIMAL_FORMAT.format(sav.points), skin);
        pointsLabel.setAlignment(Align.left);
        boardTable.add(pointsLabel).row();

        // - - -  M U L T I P L I E R  - - - :
        // Icon for multiplier label:
        Image multiplierIcon = new Image(widgetAtlas.findRegion("multiplier"));
        boardTable.add(multiplierIcon).size(24f).padLeft(6f).padRight(6f);

        // Label for multiplier:
        multiplierLabel = new Label(MaxonConstants.DECIMAL_FORMAT.format(sav.multiplier), skin);
        multiplierLabel.setAlignment(Align.left);
        boardTable.add(multiplierLabel).row();

        // - - - - - -  Q U I C K  A C T I O N S  B O A R D  - - - - - - :
        quickTable = new Table(widgetSkin);
        quickTable.setBackground("board_bg");
        quickTable.setSize(stage.getWidth(), 64f);
        quickTable.setPosition(0, 0);
        quickTable.align(Align.center);

        // - - -  S H O P  B U T T O N  - - - :
        ImageButton shopButton = new ImageButton(widgetSkin, "shop");

        shopButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isShopping && !isInventoryEnabled) {
                    showShop();
                    isShopping = true;
                }
            }
        });

        quickTable.add(shopButton).size(64f).pad(6f);

        // - - -  I N V E N T O R Y  B U T T O N  - - - :
        ImageButton inventoryButton = new ImageButton(widgetSkin, "inventory");

        inventoryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isShopping && !isInventoryEnabled) {
                    showInventory();
                    isInventoryEnabled = true;
                }
            }
        });

        quickTable.add(inventoryButton).size(64f).pad(6f);

        stage.addActor(quickTable);

        // Generate the background:
        bgTiles = new ArrayList<>();

        genNewBgTiles((int) stage.getWidth(), (int) stage.getHeight());

        // Creating the Maxon cat:
        cat = new AnimatedImage(
                SpriteUtils.splitToTextureRegions(game.assetManager.get("sprites/sheet/loadingCircle.png", Texture.class),
                        112, 112, 10, 5
                )
        );
        cat.disableAnim(); // Disable the image animation.
        maxon = new AnimatedImageButton(cat); // Make button with animated image.
        maxon.setSize(cat.getWidth() * 2f, cat.getHeight() * 2f);
        maxon.setPosition(
                (stage.getWidth() / 2f) - (maxon.getWidth() / 2f),
                (stage.getHeight() / 2f) - (maxon.getHeight() / 2f)
        );

        maxon.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                displayPointIncrease();
            }
        });

        stage.addActor(maxon);

        DebugLabel debugLabel = new DebugLabel(skin);

        debugLabel.setPosition(
                8,
                (Gdx.graphics.getHeight() - debugLabel.getHeight()) - 8
        );

        stage.addActor(debugLabel);

        notEnoughPointsDialog = new Dialog(game.locale.TranslatableText("dialogs.not_enough_points"), widgetSkin, "dialog");

        Gdx.input.setInputProcessor(new InputMultiplexer(this, new CrossProcessor(), stage));
    }

    @Override
    public void show() {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                float multiplier = 0;

                for (MaxonItem item : items) {
                    multiplier += item.multiplier;
                }

                player.points += multiplier;

                final TypingLabel label = new TypingLabel(game.locale.FormattedText("game.newPoint", MaxonConstants.DECIMAL_FORMAT.format(player.multiplier)), skin, "default");

                label.setPosition(
                        maxon.getX(),
                        maxon.getY() + maxon.getHeight()
                );

                label.setWidth(maxon.getWidth());

                label.setAlignment(Align.center);

                label.addAction(Actions.parallel(
                        Actions.fadeOut(5f),
                        Actions.moveTo(
                                label.getX(), label.getY() + Math.getRandomNumber(10, 156), 5f, Interpolation.exp5Out)
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

        // Random gifts:
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                final ImageButton gift = new ImageButton(widgetSkin, "gift");
                gift.setPosition(stage.getWidth() + gift.getWidth(), Math.getRandomNumber((int) gift.getHeight(), (int) stage.getHeight() - (int) gift.getHeight()));
                gift.addAction(
                        Actions.repeat(
                                3,
                                Actions.sequence(
                                        Actions.moveTo(-gift.getWidth(), gift.getY(), 15f, Interpolation.linear),
                                        Actions.moveTo(stage.getWidth() + gift.getWidth(), Math.getRandomNumber((int) gift.getHeight(), (int) stage.getHeight() - (int) gift.getHeight()), 15f, Interpolation.linear)
                                )
                        )
                );


                gift.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        int giftId = Math.getRandomNumber(0, 25);
                        final TypingLabel label = new TypingLabel(game.locale.TranslatableText("gifts.empty"), skin);

                        switch (giftId) {
                            // Points
                            case 0:
                                int randPoints = Math.getRandomNumber(150, 3000);
                                label.setText(game.locale.FormattedText("gifts.points", String.valueOf(randPoints)));
                                player.points += randPoints;
                                break;

                            // Multiplier
                            case 1:
                                int randMp = Math.getRandomNumber(1, 10);
                                label.setText(game.locale.FormattedText("gifts.multiplier", String.valueOf(randMp)));
                                player.multiplier += randMp;
                                break;


                            // Random pet
                            case 2:
                                int randPet = Math.getRandomNumber(0, 1);
                                assert MaxonItemRegister.get(randPet) != null;
                                String name = MaxonItemRegister.get(randPet).name;
                                label.setText(game.locale.FormattedText("gifts.new_pet", name));
                                player.inv.add(randPet);
                                if (invItems.containsKey(randPet)) {
                                    invItems.put(randPet, invItems.get(randPet) + 1);
                                } else {
                                    invItems.put(randPet, 1);
                                }
                                break;
                            // Default
                            default:
                                break;
                        }

                        label.setPosition(
                                gift.getX(),
                                gift.getY()
                        );

                        label.addAction(Actions.sequence(
                                Actions.delay(3f),
                                Actions.fadeOut(2f)
                        ));

                        stage.addActor(label);
                        gift.remove();

                        Timer.schedule(new Timer.Task() {
                            @Override
                            public void run() {
                                label.remove();
                            }
                        }, 5f);
                    }
                });

                stage.addActor(gift);
            }
        }, 600, 600);

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
                MaxonConstants.DECIMAL_FORMAT.format(player.points)
        ));

        // Update the multiplier label:
        multiplierLabel.setText(game.locale.FormattedText("game.multiplier",
                MaxonConstants.DECIMAL_FORMAT.format(player.multiplier)
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

    private void showShop() {
        // - - - - - -  S H O P  T A B L E  - - - - - - :
        final Table shopTable = new Table(widgetSkin);
        shopTable.setBackground("board_bg");
        shopTable.setSize(stage.getWidth() - 20f, stage.getHeight() - (boardTable.getHeight() + quickTable.getHeight() + 20f));
        shopTable.setPosition(10f, quickTable.getHeight() + 10f);
        shopTable.align(Align.top | Align.center);

        stage.addActor(shopTable);

        // Header table:
        Table headShopTable = new Table();
        shopTable.add(headShopTable).width(shopTable.getWidth()).row();

        // - - -  S H O P  T I T L E  - - -:
        Label shopTitle = new Label(game.locale.TranslatableText("game.petShop"), skin);
        headShopTable.add(shopTitle).expandX();

        // - - -  C L O S E  B U T T O N  - - - :
        TextButton closeButton = new TextButton("X", widgetSkin);

        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                shopTable.remove();
                isShopping = !isShopping;
            }
        });

        headShopTable.add(closeButton).row();

        // - - -  S H O P  C O N T E N T  - - - :
        Table contentTable = new Table();

        // Adding items to shop:
        for (final MaxonItem item : MaxonItemRegister.getItems()) {
            PurchaseItem p_item = new PurchaseItem(
                    skin,
                    widgetSkin,
                    item
            );

            p_item.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (player.points < item.price) {
                        notEnoughPointsDialog.show(stage);

                        Timer.schedule(new Timer.Task() {
                            @Override
                            public void run() {
                                notEnoughPointsDialog.hide(Actions.fadeOut(2f, Interpolation.smoother));
                            }
                        }, 2);

                        return;
                    }

                    player.points -= item.price;
                    player.multiplier += item.multiplier;
                    player.inv.add(item.id);

                    if (invItems.containsKey(item.id)) {
                        invItems.put(item.id, invItems.get(item.id) + 1);
                    } else {
                        invItems.put(item.id, 1);
                    }
                }
            });

            contentTable.add(p_item).pad(6f).width(shopTable.getWidth()).row();
        }

        // Scroll panel for content table:
        ScrollPane contentPane = new ScrollPane(contentTable);
        contentPane.setScrollingDisabled(true, false);
        shopTable.add(contentPane);
    }

    private void showInventory() {
        // - - - - - -  I N V E N T O R Y  T A B L E  - - - - - - :
        final Table inventoryTable = new Table(widgetSkin);
        inventoryTable.setBackground("board_bg");
        inventoryTable.setSize(stage.getWidth() - 20f, stage.getHeight() - (boardTable.getHeight() + quickTable.getHeight() + 20f));
        inventoryTable.setPosition(10f, quickTable.getHeight() + 10f);
        inventoryTable.align(Align.top | Align.center);

        stage.addActor(inventoryTable);

        // Header table:
        Table headInventoryTable = new Table();
        inventoryTable.add(headInventoryTable).width(inventoryTable.getWidth()).row();

        // - - -  S H O P  T I T L E  - - -:
        Label inventoryTitle = new Label(game.locale.TranslatableText("game.inventory.title"), skin);
        headInventoryTable.add(inventoryTitle).expandX();

        // - - -  C L O S E  B U T T O N  - - - :
        TextButton closeButton = new TextButton("X", widgetSkin);

        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                inventoryTable.remove();
                isInventoryEnabled = !isInventoryEnabled;
            }
        });

        headInventoryTable.add(closeButton).row();

        // - - -  I N V E N T O R Y  C O N T E N T  - - - :
        Table contentTable = new Table();
        contentTable.align(Align.left);

        // Adding items to inventory:
        for (int i = 0; i < invItems.keySet().size(); i++) {
            MaxonItem item = MaxonItemRegister.get(i);

            if (item != null) {
                InventoryAnimatedItem invItem = new InventoryAnimatedItem(item, skin, invItems.get(i));
                Cell<InventoryAnimatedItem> cell = contentTable.add(invItem).size(64, 64).pad(5f);

                if (i != 0 && i % (inventoryTable.getWidth() / 69f) == 0) {
                    cell.row();
                }
            }
        };

        // Scroll panel for content table:
        ScrollPane contentPane = new ScrollPane(contentTable);
        contentPane.setScrollingDisabled(true, false);
        inventoryTable.add(contentPane);
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
            player.lastTimestamp = System.currentTimeMillis();
            player.elapsedTime = (System.currentTimeMillis() - playTimestamp) + player.elapsedTime;
            GameDataSystem.save(player, String.format("0%s.maxon", (slotId >= 0) ? slotId : "latest"));

            game.setScreen(new MenuScreen(game));
            dispose();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            displayPointIncrease();
        }
        return false;
    }

    private void displayPointIncrease() {
        cat.nextFrame();
        maxon.setDrawable(cat.getDrawable());

        player.points += player.multiplier;

        final TypingLabel label = new TypingLabel(game.locale.FormattedText("game.newPoint", MaxonConstants.DECIMAL_FORMAT.format(player.multiplier)), skin, "default");

        label.setPosition(
                maxon.getX(),
                maxon.getY() + maxon.getHeight()
        );

        label.setWidth(maxon.getWidth());

        label.setAlignment(Align.center);

        label.addAction(Actions.parallel(
                Actions.fadeOut(5f),
                Actions.moveTo(
                        label.getX(), label.getY() + Math.getRandomNumber(10, 156), 5f, Interpolation.exp5Out)
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
