package kz.ilotterytea.maxon.teavm;

import com.github.xpenatan.gdx.backends.teavm.TeaApplication;
import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration;
import kz.ilotterytea.maxon.MaxonGame;

/**
 * Launches the TeaVM/HTML application.
 */
public class TeaVMLauncher {
    public static void main(String[] args) {
        TeaApplicationConfiguration config = new TeaApplicationConfiguration("canvas");
        //// If width and height are each greater than 0, then the app will use a fixed size.
        //config.width = 800;
        //config.height = 600;
        //// If width and height are both 0, then the app will use all available space.
        //config.width = 0;
        //config.height = 0;
        //// If width and height are both -1, then the app will fill the canvas size.
        config.width = -1;
        config.height = -1;
        new TeaApplication(MaxonGame.getInstance(), config);
    }
}
