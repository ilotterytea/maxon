package kz.ilotterytea.maxon.pets;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import kz.ilotterytea.maxon.MaxonConstants;
import kz.ilotterytea.maxon.utils.formatters.NumberFormatter;

public class PetWidget extends Table {
    private double price;
    private final Skin skin;
    private final Label priceLabel, nameLabel;
    private TextTooltip priceTooltip, nameTooltip;
    private final Pet pet;

    private boolean isDisabled = false;

    private final Label.LabelStyle idleStyle, hoverStyle, disabledStyle, availablePriceStyle, disabledPriceStyle;

    public PetWidget(
            Skin skin,
            Pet pet,
            TextureAtlas atlas
    ) {
        super(skin);
        super.setBackground("store_item");
        super.align(Align.left | Align.center);
        this.pet = pet;
        this.skin = skin;

        super.add(pet.getIcon()).size(64f).pad(6f);

        this.idleStyle = skin.get("store_item", Label.LabelStyle.class);
        this.hoverStyle = skin.get("store_item_hover", Label.LabelStyle.class);
        this.disabledStyle = skin.get("store_item_disabled", Label.LabelStyle.class);
        this.availablePriceStyle = skin.get("store_item_price", Label.LabelStyle.class);
        this.disabledPriceStyle = skin.get("store_item_price_disabled", Label.LabelStyle.class);

        this.price = pet.getPrice();

        Table summary = new Table(skin);
        summary.align(Align.left);

        this.nameLabel = new Label(pet.getName(), skin, "store_item");
        nameLabel.setAlignment(Align.left);

        this.nameTooltip = new TextTooltip(pet.getDescription(), skin);
        nameTooltip.setInstant(true);
        nameLabel.addListener(nameTooltip);

        Image priceIcon = new Image(atlas.findRegion("points"));

        this.priceLabel = new Label(NumberFormatter.format((long) price), skin, "store_item_price");
        priceLabel.setAlignment(Align.left);

        priceTooltip = new TextTooltip(MaxonConstants.DECIMAL_FORMAT.format(pet.getPrice()), skin);
        priceTooltip.setInstant(true);
        priceLabel.addListener(priceTooltip);

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
                    PetWidget.super.setBackground("store_item_hover");
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                if (!isDisabled) {
                    nameLabel.setStyle(idleStyle);
                    PetWidget.super.setBackground("store_item");
                }
            }
        });
    }

    public void setPrice(double price) {
        if (price == this.price) {
            return;
        }

        this.price = price;
        this.priceLabel.setText(NumberFormatter.format((long) price));

        priceTooltip.hide();
        this.priceTooltip = new TextTooltip(MaxonConstants.DECIMAL_FORMAT.format(price), skin);
        priceTooltip.setInstant(true);

        priceLabel.clearListeners();
        priceLabel.addListener(priceTooltip);
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

    public Pet getPet() {
        return pet;
    }
}
