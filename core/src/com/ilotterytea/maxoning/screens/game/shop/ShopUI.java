package com.ilotterytea.maxoning.screens.game.shop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.ilotterytea.maxoning.player.MaxonItem;
import com.ilotterytea.maxoning.player.MaxonItemRegister;
import com.ilotterytea.maxoning.player.MaxonSavegame;
import com.ilotterytea.maxoning.ui.PurchaseItem;
import com.ilotterytea.maxoning.utils.math.Math;

import java.util.ArrayList;

public class ShopUI {
    private final Stage stage;
    private final Skin skin;
    private final TextureAtlas atlas;
    private final ArrayList<MaxonItem> items;

    private ShopMode mode;
    private ShopMultiplier multiplier;

    public ShopUI(Stage stage, Skin skin, TextureAtlas atlas) {
        this.stage = stage;
        this.skin = skin;
        this.atlas = atlas;
        this.mode = ShopMode.BUY;
        this.multiplier = ShopMultiplier.X1;
        this.items = MaxonItemRegister.getItems();
    }

    public void createSavegameUI(final MaxonSavegame player) {
        Table table = new Table(this.skin);
        table.setBackground("board");

        table.setWidth(Math.percentFromValue(25f, Gdx.graphics.getWidth()));
        table.setHeight(Math.percentFromValue(15f, Gdx.graphics.getHeight()));
        table.align(Align.center | Align.left);
        table.pad(10f);

        // Setting up the points
        Table pointsTable = new Table();

        Image pointsImage = new Image(this.atlas.findRegion("points"));
        Label pointsLabel = new Label(String.valueOf(player.points), this.skin);

        pointsTable.add(pointsImage);
        pointsTable.add(pointsLabel).padLeft(15f);

        table.add(pointsTable).padBottom(10f).row();

        // Setting up the multiplier
        Table multiplierTable = new Table();

        Image multiplierImage = new Image(this.atlas.findRegion("multiplier"));
        Label multiplierLabel = new Label(String.format("%s/s", player.multiplier), this.skin);

        multiplierTable.add(multiplierImage);
        multiplierTable.add(multiplierLabel).padLeft(15f);

        table.add(multiplierTable);

        this.stage.addActor(table);
    }

    public void createShopTitleUI() {
        Table table = new Table(this.skin);
        table.setBackground("board");

        table.setWidth(Math.percentFromValue(25f, Gdx.graphics.getWidth()));
        table.setHeight(Math.percentFromValue(5f, Gdx.graphics.getHeight()));
        table.setY(Gdx.graphics.getHeight() - table.getHeight());
        table.align(Align.center);
        table.pad(10f);

        Label label = new Label("Store", skin);
        table.add(label);

        this.stage.addActor(table);
    }

    public void createShopControlUI() {
        Table table = new Table(this.skin);
        table.setBackground("board");

        table.setWidth(Math.percentFromValue(25f, Gdx.graphics.getWidth()));
        table.setHeight(Math.percentFromValue(10f, Gdx.graphics.getHeight()));
        table.setY(Gdx.graphics.getHeight() - table.getHeight() - Math.percentFromValue(5f, Gdx.graphics.getHeight()));
        table.align(Align.center);
        table.pad(10f);

        // Mode changer
        Table modeTable = new Table();

        TextButton buyButton = new TextButton("Buy", this.skin);
        buyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                mode = ShopMode.BUY;
            }
        });
        modeTable.add(buyButton).growX().row();

        TextButton sellButton = new TextButton("Sell", this.skin);
        buyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                mode = ShopMode.SELL;
            }
        });
        modeTable.add(sellButton).growX();

        table.add(modeTable).grow();

        // Multiplier changer
        Table multiplierTable = new Table();

        TextButton x1Button = new TextButton("1x", this.skin);
        x1Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                multiplier = ShopMultiplier.X1;
            }
        });
        multiplierTable.add(x1Button).width(64f).height(64f).padRight(10f);

        TextButton x10Button = new TextButton("10x", this.skin);
        x10Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                multiplier = ShopMultiplier.X10;
            }
        });
        multiplierTable.add(x10Button).width(64f).height(64f);

        table.add(multiplierTable).grow();

        this.stage.addActor(table);
    }

    public void createShopListUI(final MaxonSavegame player) {
        Table table = new Table();

        table.setWidth(Math.percentFromValue(25f, Gdx.graphics.getWidth()));
        table.setHeight(Math.percentFromValue(70f, Gdx.graphics.getHeight()));
        table.setY(Math.percentFromValue(15f, Gdx.graphics.getHeight()));
        table.align(Align.center);
        table.pad(10f);

        for (final MaxonItem item : this.items) {
            int amount = (int) player.inv.stream().filter(c -> c == item.id).count();

            double price = item.price * java.lang.Math.pow(1.15f, amount + this.multiplier.getMultiplier());
            item.price = (float) price;
            PurchaseItem purchaseItem = new PurchaseItem(this.skin, item);

            table.add(purchaseItem).width(table.getWidth()).padBottom(5f).row();
        }

        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setScrollingDisabled(true, false);

        Table scrollPaneTable = new Table(this.skin);
        scrollPaneTable.setBackground("shop_list");
        scrollPaneTable.setPosition(table.getX(), table.getY());
        scrollPaneTable.setSize(table.getWidth(), table.getHeight());
        scrollPaneTable.pad(1f, 5f, 1f, 5f);
        scrollPaneTable.add(scrollPane).grow();

        this.stage.addActor(scrollPaneTable);
    }
}
