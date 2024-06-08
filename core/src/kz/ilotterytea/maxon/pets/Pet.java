package kz.ilotterytea.maxon.pets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;
import kz.ilotterytea.maxon.MaxonConstants;
import kz.ilotterytea.maxon.MaxonGame;
import kz.ilotterytea.maxon.anim.SpriteUtils;
import kz.ilotterytea.maxon.ui.AnimatedImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pet {
    private final String id, name, description;
    private final double price, multiplier;
    private final AnimatedImage icon;
    private static final Logger logger = LoggerFactory.getLogger(Pet.class);

    private Pet(String id, String name, String description, double price, double multiplier, AnimatedImage icon) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.multiplier = multiplier;
        this.icon = icon;
    }

    public static Pet create(String id, double price, double multiplier, int iconColumns, int iconRows) {
        MaxonGame game = MaxonGame.getInstance();
        TextureRegion[] regions;

        try {
            Texture texture = game.assetManager.get("sprites/pets/" + id + ".png", Texture.class);
            regions = SpriteUtils.splitToTextureRegions(
                    texture,
                    texture.getWidth() / iconColumns,
                    texture.getHeight() / iconRows,
                    iconColumns,
                    iconRows
            );

        } catch (GdxRuntimeException e) {
            logger.warn("Failed to load icon spritesheet for ID {}", id);
            regions = new TextureRegion[]{new TextureRegion(MaxonConstants.MISSING_TEXTURE)};
        }

        AnimatedImage icon = new AnimatedImage(regions);

        String name = game.locale.TranslatableText("pet." + id + ".name");
        if (name == null) {
            name = "pet." + id + ".name";
        }

        String description = game.locale.TranslatableText("pet." + id + ".desc");
        if (description == null) {
            description = "pet." + id + ".desc";
        }

        return new Pet(id, name, description, price, multiplier, icon);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public AnimatedImage getIcon() {
        return icon;
    }
}
