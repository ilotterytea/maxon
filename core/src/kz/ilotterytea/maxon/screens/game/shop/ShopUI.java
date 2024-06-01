package kz.ilotterytea.maxon.screens.game.shop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import kz.ilotterytea.maxon.player.MaxonItem;
import kz.ilotterytea.maxon.player.MaxonItemRegister;
import kz.ilotterytea.maxon.player.MaxonSavegame;
import kz.ilotterytea.maxon.ui.PurchaseItem;
import kz.ilotterytea.maxon.utils.formatters.NumberFormatter;
import kz.ilotterytea.maxon.utils.math.Math;

import java.util.ArrayList;

public class ShopUI {
    private final Table table, mainTable;
    private final Skin skin;
    private final TextureAtlas atlas;
    private final ArrayList<MaxonItem> items;

    private ShopMode mode;
    private ShopMultiplier multiplier;

    private final MaxonSavegame savegame;
    private Label pointsLabel, multiplierLabel;

    private final ArrayList<PurchaseItem> purchaseItems = new ArrayList<>();

    public ShopUI(final MaxonSavegame savegame, Stage stage, Skin skin, TextureAtlas atlas) {
        this.savegame = savegame;

        this.skin = skin;
        this.atlas = atlas;
        this.mode = ShopMode.BUY;
        this.multiplier = ShopMultiplier.X1;
        this.items = MaxonItemRegister.getItems();

        this.table = new Table(skin);
        this.table.setBackground("store");

        this.mainTable = new Table(this.skin);
        mainTable.setFillParent(true);
        mainTable.align(Align.center | Align.left);

        mainTable.add(this.table).growY().width(Math.percentFromValue(25f, Gdx.graphics.getWidth()));
        stage.addActor(mainTable);
    }

    public void createSavegameUI() {
        Table table = new Table(this.skin);

        table.align(Align.center | Align.left);
        table.pad(10f);

        // Setting up the points
        Table pointsTable = new Table();
        pointsTable.align(Align.left);

        Image pointsImage = new Image(this.atlas.findRegion("points"));
        this.pointsLabel = new Label(String.valueOf(savegame.points), this.skin);
        pointsLabel.setAlignment(Align.left);

        pointsTable.add(pointsImage).size(64f, 64f).padRight(15f);
        pointsTable.add(pointsLabel).grow();

        table.add(pointsTable).grow().padBottom(5f).row();

        // Setting up the multiplier
        Table multiplierTable = new Table();
        multiplierTable.align(Align.left);

        Image multiplierImage = new Image(this.atlas.findRegion("multiplier"));
        this.multiplierLabel = new Label(String.format("%s/s", savegame.multiplier), this.skin);
        multiplierLabel.setAlignment(Align.left);

        multiplierTable.add(multiplierImage).size(64f, 64f).padRight(15f);
        multiplierTable.add(multiplierLabel).grow();

        table.add(multiplierTable).grow();

        this.table.add(table).grow();
    }

    public void createShopTitleUI() {
        Table table = new Table();

        Label label = new Label("Store", skin);
        label.setAlignment(Align.center);
        table.add(label).pad(10f).grow();

        this.table.add(table).growX().row();
    }

    public void createShopControlUI() {
        Table table = new Table(this.skin);
        table.setBackground("store_control");

        table.align(Align.center);
        table.pad(10f);

        // Mode changer
        Table modeTable = new Table();

        TextButton buyButton = new TextButton("Buy", this.skin, "store_control");
        buyButton.setDisabled(true);
        modeTable.add(buyButton).padBottom(5f).growX().row();

        TextButton sellButton = new TextButton("Sell", this.skin, "store_control");
        modeTable.add(sellButton).growX();

        sellButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                mode = ShopMode.SELL;
                sellButton.setDisabled(true);
                buyButton.setDisabled(false);
            }
        });

        buyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                mode = ShopMode.BUY;
                sellButton.setDisabled(false);
                buyButton.setDisabled(true);
            }
        });

        table.add(modeTable).padRight(5f).grow();

        // Multiplier changer
        Table multiplierTable = new Table();
        multiplierTable.align(Align.left);

        TextButton x1Button = new TextButton("1x", this.skin, "store_control");
        x1Button.setDisabled(true);
        multiplierTable.add(x1Button).width(64f).height(64f).padRight(10f);

        TextButton x10Button = new TextButton("10x", this.skin, "store_control");
        multiplierTable.add(x10Button).width(64f).height(64f);

        x1Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                multiplier = ShopMultiplier.X1;
                x1Button.setDisabled(true);
                x10Button.setDisabled(false);
            }
        });

        x10Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                multiplier = ShopMultiplier.X10;
                x1Button.setDisabled(false);
                x10Button.setDisabled(true);
            }
        });

        table.add(multiplierTable).grow();

        this.table.add(table).grow().row();
    }

    public void createShopListUI() {
        Table table = new Table(this.skin);

        for (final MaxonItem item : this.items) {
            PurchaseItem purchaseItem = new PurchaseItem(this.skin, item, this.atlas);
            purchaseItem.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);

                    if (purchaseItem.isDisabled()) {
                        return;
                    }

                    if (mode == ShopMode.BUY) {
                        savegame.points -= (long) purchaseItem.getPrice();
                        for (int i = 0; i < multiplier.getMultiplier(); i++) {
                            savegame.inv.add(purchaseItem.getItem().id);
                        }
                    } else {
                        savegame.points += (long) purchaseItem.getPrice();
                        for (int i = 0; i < multiplier.getMultiplier(); i++) {
                            savegame.inv.remove(Integer.valueOf(purchaseItem.getItem().id));
                        }
                    }
                }
            });

            purchaseItems.add(purchaseItem);
            table.add(purchaseItem).growX().padBottom(5f).row();
        }

        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setScrollingDisabled(true, false);

        Table scrollPaneTable = new Table(this.skin);
        scrollPaneTable.setBackground("store_list");
        scrollPaneTable.pad(4f, 0f, 4f, 0f);
        scrollPaneTable.add(scrollPane).grow();

        this.table.add(scrollPaneTable).grow().row();
    }

    private void updatePurchaseItems() {
        for (final PurchaseItem item : this.purchaseItems) {
            int amount = (int) savegame.inv.stream().filter(c -> c == item.getItem().id).count();
            double price = item.getItem().price * java.lang.Math.pow(1.15f, amount + multiplier.getMultiplier());

            if (mode == ShopMode.SELL) {
                price /= 4;
            }

            item.setPrice(price);

            if (mode == ShopMode.BUY) {
                if (price > savegame.points || savegame.points - price < 0) {
                    item.setDisabled(true);
                } else if (item.isDisabled()) {
                    item.setDisabled(false);
                }
            } else {
                if (amount - multiplier.getMultiplier() < 0) {
                    item.setDisabled(true);
                } else if (item.isDisabled()) {
                    item.setDisabled(false);
                }
            }
        }
    }

    public void render() {
        this.pointsLabel.setText(NumberFormatter.format(savegame.points));
        this.multiplierLabel.setText(String.format("%s/s", NumberFormatter.format(savegame.multiplier)));
        updatePurchaseItems();
    }

    public void update() {
        this.mainTable.clear();
        this.mainTable.add(this.table).growY().width(Math.percentFromValue(30f, Gdx.graphics.getWidth()));
    }
}