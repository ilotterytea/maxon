package kz.ilotterytea.maxon.session;

import kz.ilotterytea.maxon.MaxonGame;
import kz.ilotterytea.maxon.shared.Acknowledge;

public class SessionHandlers {
    private static final SessionClient client = MaxonGame.getInstance().getSessionClient();

    public static void handleAcknowledge(Acknowledge acknowledge) {
    }
}
