package kz.ilotterytea.maxon.ui;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import kz.ilotterytea.maxon.player.MaxonItem;

public class InventoryAnimatedItem extends Stack {
    public InventoryAnimatedItem(
            MaxonItem item,
            Skin skin,
            Integer amount
    ) {
        super(new Image(item.icon.getDrawable()));

        Table table = new Table();
        table.setSize(super.getWidth(), super.getHeight());
        table.add(new Label(String.format("x%s", amount), skin, "default")).bottom().right();

        TextTooltip.TextTooltipStyle style = new TextTooltip.TextTooltipStyle();
        style.label = new Label.LabelStyle();
        style.label.font = skin.getFont("small");
        style.label.fontColor = skin.getColor("white");

        super.add(table);
        super.addListener(new TextTooltip(String.format("%s (%s)", item.name, item.multiplier), style));
    }
}
