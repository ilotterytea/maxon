package com.ilotterytea.maxoning.ui;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.ilotterytea.maxoning.MaxonConstants;
import com.ilotterytea.maxoning.player.MaxonItem;

public class PurchaseItem extends Table {
    public PurchaseItem(
            Skin skin,
            MaxonItem item
    ) {
        super(skin);
        super.setBackground("shop_item");
        super.align(Align.left | Align.center);

        super.add(item.icon).size(64f).pad(6f);

        Table summary = new Table();
        summary.align(Align.topLeft);

        Label name = new Label(item.name, skin, "item_title");
        name.setAlignment(Align.left);

        Label desc = new Label(String.format("%s SQP (%s/click)", MaxonConstants.DECIMAL_FORMAT.format(item.price), MaxonConstants.DECIMAL_FORMAT.format(item.multiplier)), skin, "item_price");
        desc.setAlignment(Align.left);

        summary.add(name).align(Align.left).row();
        summary.add(desc).grow();

        super.add(summary).grow();
    }
}
