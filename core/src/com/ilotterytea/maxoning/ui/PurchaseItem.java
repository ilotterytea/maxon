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
        super.setBackground("up");
        super.align(Align.left | Align.center);

        super.add(item.icon).size(81f).pad(6f);

        Table summary = new Table();
        summary.align(Align.topLeft);

        Label name = new Label(String.format("%s ($%s) (x%s/click)", item.name, MaxonConstants.DECIMAL_FORMAT.format(item.price), MaxonConstants.DECIMAL_FORMAT.format(item.multiplier)), skin);
        name.setAlignment(Align.left);

        Label desc = new Label(item.desc, skin);
        desc.setAlignment(Align.left);

        summary.add(name).width(desc.getWidth()).row();
        summary.add(desc);

        super.add(summary);
    }
}
