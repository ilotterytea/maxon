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

        Table summary = new Table();
        summary.setHeight(super.getHeight());

        Label title = new Label(String.format("%s\n(%s)", name, price), skin, "purchaseitem_title");

        summary.add(title).fillX().row();

        Label description = new Label(desc, skin, "purchaseitem_desc");
        description.setWrap(true);

        summary.add(description).fillX().row();

        Table main = new Table();
        main.add(icon).size(81, 81).left().pad(5f);
        main.add(summary).fillY().fillX().right().pad(5f);

        main.align(Align.left);

        super.addActor(main);
    }
}
