package com.ilotterytea.maxoning.ui;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;

public class PurchaseItem extends Stack {
    public PurchaseItem(
            Skin skin,
            NinePatch ninepatch,
            AnimatedImage icon,
            CharSequence name,
            CharSequence desc,
            float price
    ) {
        super(new Image(ninepatch));

        Label title = new Label(name, skin, "purchaseitem_title");
        Label description = new Label(desc, skin, "purchaseitem_desc");
        Label cost = new Label(price + "S", skin, "purchaseitem_price");

        title.setAlignment(Align.center);
        description.setAlignment(Align.center);
        cost.setAlignment(Align.center);

        description.setWrap(true);

        Table table = new Table();

        table.setPosition(0 , super.getHeight());
        table.setWidth(super.getWidth());

        table.add(icon).pad(8).center().row();
        table.add(title).expand().padBottom(8).center().row();
        table.add(description).expand().fillX().center().row();
        table.add(cost).expand().fillX().center().row();

        super.addActor(table);
    }
}
