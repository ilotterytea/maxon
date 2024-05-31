package kz.ilotterytea.maxon.ui;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class NinepatchButton extends TextButton {
    public NinepatchButton(
            NinePatch up,
            NinePatch down,
            NinePatch over,
            String text,
            Skin skin,
            String styleName
    ) {
        super(text, skin, styleName);
        TextButtonStyle style = new TextButtonStyle();

        style.up = new NinePatchDrawable(up);
        style.down = new NinePatchDrawable(down);
        style.over = new NinePatchDrawable(over);
        style.fontColor = skin.getColor("white");
        style.font = skin.getFont("default");

        super.setStyle(style);
    }
}
