package kz.ilotterytea.maxoning;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MaxoningGame extends Game {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private static MaxoningGame instance;

    public SpriteBatch getBatch() {
        return batch;
    }

    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    public static MaxoningGame getInstance() {
        return instance;
    }

    public MaxoningGame() {
        instance = this;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        setScreen(new FirstScreen());
    }
}