package com.ilotterytea.maxoning.ui;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;

public class SupaIconButton extends Stack {

    public SupaIconButton(
            NinePatch ninepatch,
            CharSequence text,
            Skin skin
    ) {
        super(new Image(ninepatch));

        Label label = new Label(text, skin);
        Table table = new Table();

        label.setAlignment(Align.center);

        table.add(label).expand().fillX().center().left();
        super.add(table);
    }
}
