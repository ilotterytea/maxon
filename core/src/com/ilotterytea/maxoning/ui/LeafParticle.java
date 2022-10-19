package com.ilotterytea.maxoning.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class LeafParticle extends Sprite {
    private float angle, x, y, vertAngle, rotation, time;

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
        this.time = Gdx.graphics.getDeltaTime();
        this.x -= (float) Math.sin(time) * this.angle;
        this.y -= (float) Math.sin(time) * this.vertAngle;

        super.setPosition(x, y);
        super.setRotation(super.getRotation() + this.rotation);
        super.draw(batch);
    }
}
