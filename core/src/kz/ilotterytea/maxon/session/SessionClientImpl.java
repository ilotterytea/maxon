package kz.ilotterytea.maxon.session;

import com.badlogic.gdx.utils.Disposable;

public interface SessionClientImpl extends Disposable {
    void send(Object o);

    void updateIdentity();

    void connect();

    void close();

    void close(int closeCode, String reason);
}
