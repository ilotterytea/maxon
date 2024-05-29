package com.ilotterytea.maxoning.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.ilotterytea.maxoning.MaxonConstants;
import com.ilotterytea.maxoning.player.MaxonItem;

public class PurchaseItem extends Table {
    private double price;
    private final Label priceLabel;
    private final MaxonItem item;

    private boolean isDisabled = false;

    public PurchaseItem(
            Skin skin,
            MaxonItem item
    ) {
        super(skin);
        super.setBackground("shop_item");
        super.align(Align.left | Align.center);

        super.add(item.icon).size(64f).pad(6f);

        this.price = item.price;
        this.item = item;

        Table summary = new Table();
        summary.align(Align.topLeft);

        Label name = new Label(item.name, skin, "item_title");
        name.setAlignment(Align.left);

        this.priceLabel = new Label(String.format("%s SQP (%s/click)", MaxonConstants.DECIMAL_FORMAT.format(price), MaxonConstants.DECIMAL_FORMAT.format(item.multiplier)), skin, "item_price");
        this.priceLabel.setAlignment(Align.left);

        summary.add(name).align(Align.left).row();
        summary.add(this.priceLabel).grow();

        super.add(summary).grow();

        super.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                if (!isDisabled) {
                    PurchaseItem.super.setBackground("shop_item_hover");
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                if (!isDisabled) {
                    PurchaseItem.super.setBackground("shop_item");
                }
            }
        });
    }

    public void setPrice(double price) {
        this.price = price;
        this.priceLabel.setText(String.format("%s SQP (%s/click)", MaxonConstants.DECIMAL_FORMAT.format(price), MaxonConstants.DECIMAL_FORMAT.format(item.multiplier)));
    }

    public double getPrice() {
        return price;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public void setDisabled(boolean disabled) {
        isDisabled = disabled;

        super.setBackground(isDisabled ? "bg" : "shop_item");
    }

    public MaxonItem getItem() {
        return item;
    }
}
