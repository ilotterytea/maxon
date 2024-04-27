package com.ilotterytea.maxoning.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

public class DecalPlayer {
    private final TextureRegion[] regions;
    private int regionIndex;
    private final Decal decal;
    private final BoundingBox box;

    public DecalPlayer(TextureRegion[] regions) {
        this.regions = regions;
        this.regionIndex = 0;

        this.decal = Decal.newDecal(this.regions[this.regionIndex]);
        this.decal.setScale(0.025f);
        this.decal.setPosition(-4.0f, 1.75f, -4.0f);

        float width = this.decal.getWidth() / (this.decal.getScaleX() * 1000f);
        float height = this.decal.getHeight() / (this.decal.getScaleY() * 1000f);

        Vector3 position = this.decal.getPosition();
        Vector3 minBox = new Vector3(position.x - width / 3, position.y - height / 3, position.z - width / 3);
        Vector3 maxBox = new Vector3(position.x + width / 3, position.y + height / 3, position.z + width / 3);

        this.box = new BoundingBox(minBox, maxBox);
    }

    public void render(Camera camera) {
        checkCollisions(camera);
    }

    private void checkCollisions(Camera camera) {
        Ray ray = null;

        if (Gdx.input.justTouched()) {
            ray = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
        }

        if (ray == null) {
            return;
        }

        Vector3 intersection = new Vector3();

        if (Intersector.intersectRayBounds(ray, box, intersection)) {
            updateTextureRegion();
        }
    }

    private void updateTextureRegion() {
        this.regionIndex++;

        if (this.regions[this.regionIndex] == null) {
            this.regionIndex = 0;
        }

        this.decal.setTextureRegion(this.regions[this.regionIndex]);
    }

    public Decal getDecal() {
        return this.decal;
    }
}
