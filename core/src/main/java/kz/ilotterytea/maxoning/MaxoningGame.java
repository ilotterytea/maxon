package kz.ilotterytea.maxoning;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MaxoningGame extends Game {
    @Override
    public void create() {
        setScreen(new FirstScreen());
    }
}