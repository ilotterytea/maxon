package kz.ilotterytea.maxon.ui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import kz.ilotterytea.maxon.player.MaxonItem;
import kz.ilotterytea.maxon.utils.formatters.NumberFormatter;

public class PurchaseItem extends Table {
    private double price;
    private final Label priceLabel, nameLabel;
    private final MaxonItem item;

    private boolean isDisabled = false;

    private final Label.LabelStyle idleStyle, hoverStyle, disabledStyle, availablePriceStyle, disabledPriceStyle;

    public PurchaseItem(
            Skin skin,
            MaxonItem item,
            TextureAtlas atlas
    ) {
        super(skin);
        super.setBackground("store_item");
        super.align(Align.left | Align.center);

        super.add(item.icon).size(64f).pad(6f);

        this.idleStyle = skin.get("store_item", Label.LabelStyle.class);
        this.hoverStyle = skin.get("store_item_hover", Label.LabelStyle.class);
        this.disabledStyle = skin.get("store_item_disabled", Label.LabelStyle.class);
        this.availablePriceStyle = skin.get("store_item_price", Label.LabelStyle.class);
        this.disabledPriceStyle = skin.get("store_item_price_disabled", Label.LabelStyle.class);

        this.price = item.price;
        this.item = item;

        Table summary = new Table(skin);
        summary.align(Align.left);

        this.nameLabel = new Label(item.name, skin, "store_item");
        nameLabel.setAlignment(Align.left);

        Image priceIcon = new Image(atlas.findRegion("points"));

        this.priceLabel = new Label(NumberFormatter.format((long) price), skin, "store_item_price");
        priceLabel.setAlignment(Align.left);

        summary.add(nameLabel).align(Align.left).grow().row();

        Table priceTable = new Table();
        priceTable.align(Align.left);
        priceTable.add(priceIcon).size(16f, 16f).padRight(5f);
        priceTable.add(priceLabel).grow();
        summary.add(priceTable).grow();

        super.add(summary).align(Align.left).grow();

        super.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                if (!isDisabled) {
                    nameLabel.setStyle(hoverStyle);
                    PurchaseItem.super.setBackground("store_item_hover");
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                if (!isDisabled) {
                    nameLabel.setStyle(idleStyle);
                    PurchaseItem.super.setBackground("store_item");
                }
            }
        });
    }

    public void setPrice(double price) {
        this.price = price;
        this.priceLabel.setText(NumberFormatter.format((long) price));
    }

    public double getPrice() {
        return price;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public void setDisabled(boolean disabled) {
        isDisabled = disabled;

        priceLabel.setStyle(isDisabled ? disabledPriceStyle : availablePriceStyle);
        nameLabel.setStyle(isDisabled ? disabledStyle : idleStyle);
        super.setBackground(isDisabled ? "store_item_disabled" : "store_item");
    }

    public MaxonItem getItem() {
        return item;
    }
}
