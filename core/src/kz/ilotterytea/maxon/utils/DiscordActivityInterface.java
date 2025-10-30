package kz.ilotterytea.maxon.utils;

import com.badlogic.gdx.utils.Disposable;

public interface DiscordActivityInterface extends Disposable {
    void init();

    void updateActivity();

    void runThread();
}
