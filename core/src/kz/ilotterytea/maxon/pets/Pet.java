package kz.ilotterytea.maxon.pets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.utils.GdxRuntimeException;
import kz.ilotterytea.maxon.MaxonConstants;
import kz.ilotterytea.maxon.MaxonGame;
import kz.ilotterytea.maxon.anim.SpriteUtils;
import kz.ilotterytea.maxon.ui.AnimatedImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Pet {
    private final String id, name, description;
    private final double price, multiplier;
    private final AnimatedImage icon;
    private final Decal decal;
    private static final Logger logger = LoggerFactory.getLogger(Pet.class);

    private Pet(String id, String name, String description, double price, double multiplier, AnimatedImage icon, Decal decal) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.multiplier = multiplier;
        this.icon = icon;
        this.decal = decal;
    }

    public static Pet create(String id, double price, double multiplier, int iconColumns, int iconRows) {
        MaxonGame game = MaxonGame.getInstance();
        ArrayList<TextureRegion> regions;

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
            regions = new ArrayList<>(List.of(new TextureRegion(MaxonConstants.MISSING_TEXTURE)));
        }

        AnimatedImage icon = new AnimatedImage(regions, 5);

        String name = game.locale.TranslatableText("pet." + id + ".name");
        if (name == null) {
            name = "pet." + id + ".name";
        }

        String description = game.locale.TranslatableText("pet." + id + ".desc");
        if (description == null) {
            description = "pet." + id + ".desc";
        }

        Decal decal = Decal.newDecal(0.5f, 0.5f, regions.get(0), true);

        return new Pet(id, name, description, price, multiplier, icon, decal);
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

    public Decal getDecal() {
        return decal;
    }
}
