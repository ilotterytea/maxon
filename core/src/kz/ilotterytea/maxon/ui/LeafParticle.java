package kz.ilotterytea.maxon.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class LeafParticle extends Sprite {
    private final float angle;
    private float x;
    private float y;
    private final float vertAngle;
    private final float rotation;

    public LeafParticle(TextureRegion region, float x, float y, float angle, float vertAngle, float rotation) {
        super(region);
        this.angle = angle;
        this.vertAngle = vertAngle;
        this.rotation = rotation;
        this.x = x;
        this.y = y;
    }

    @Override
    public void draw(Batch batch) {
        float time = Gdx.graphics.getDeltaTime();
        this.x -= (float) Math.sin(time) * this.angle;
        this.y -= (float) Math.sin(time) * this.vertAngle;

        super.setPosition(x, y);
        super.setRotation(super.getRotation() + this.rotation);
        super.draw(batch);
    }
}
