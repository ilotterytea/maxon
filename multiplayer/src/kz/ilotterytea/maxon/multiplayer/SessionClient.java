package kz.ilotterytea.maxon.multiplayer;

import com.badlogic.gdx.utils.*;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.WebSockets;
import kz.ilotterytea.maxon.MaxonConstants;
import kz.ilotterytea.maxon.MaxonGame;
import kz.ilotterytea.maxon.screens.MenuScreen;
import kz.ilotterytea.maxon.session.SessionClientImpl;
import kz.ilotterytea.maxon.session.SessionHandlers;
import kz.ilotterytea.maxon.shared.Acknowledge;
import kz.ilotterytea.maxon.shared.exceptions.PlayerKickException;

public class SessionClient implements SessionClientImpl, WebSocketListener {
    private final Logger log;
    private final MaxonGame game;
    private WebSocket socket;

    public SessionClient() {
        this.socket = WebSockets.newSocket(MaxonConstants.SESSION_WSS_URL);
        this.socket.addListener(this);

        this.log = new Logger(SessionClient.class.getName());
        this.game = MaxonGame.getInstance();
    }

    @Override
    public boolean onOpen(WebSocket webSocket) {
        log.info("Connected!");
        updateIdentity();
        return true;
    }

    @Override
    public boolean onClose(WebSocket webSocket, int closeCode, String reason) {
        log.info(String.format("Connection closed! Reason: %d %s", closeCode, reason));
        game.getIdentityClient().invalidateToken();
        if (!game.getScreen().getClass().equals(MenuScreen.class)) {
            game.setScreen(new MenuScreen());
        }
        return true;
    }

    @Override
    public boolean onMessage(WebSocket webSocket, String packet) {
        try {
            Json json = new Json();
            JsonValue root = new JsonReader().parse(packet);

            String type = root.getString("type");
            JsonValue data = root.get("data");

            Object obj = switch (type) {
                case "Acknowledge" -> json.readValue(Acknowledge.class, data);
                case "PlayerKickException" -> json.readValue(PlayerKickException.class, data);
                default -> null;
            };

            if (obj instanceof Acknowledge acknowledge) SessionHandlers.handleAcknowledge(acknowledge);
            else if (obj instanceof PlayerKickException exception) throw exception;
        } catch (PlayerKickException e) {
            log.info("Kicked out!", e);
        } catch (Exception e) {
            log.error("An exception was thrown while processing message", e);
        }
        return false;
    }

    @Override
    public boolean onMessage(WebSocket webSocket, byte[] packet) {
        return false;
    }

    @Override
    public boolean onError(WebSocket webSocket, Throwable error) {
        log.error("Error occurred on session websocket", error);
        return true;
    }

    @Override
    public void updateIdentity() {
        //IdentityClient identityClient = game.getIdentityClient();

        //Identity identity = new Identity(identityClient.getClientToken(), identityClient.getAccessToken());

        //send(identity);
    }

    @Override
    public void send(Object object) {
        try {
            ObjectMap<String, Object> data = new ObjectMap<>();
            data.put("type", object.getClass().getSimpleName());
            data.put("data", object);
            socket.send(new Json().toJson(data));
        } catch (Exception e) {
            log.error("Failed to serialize and send an object", e);
        }
    }

    @Override
    public void connect() {
        socket.connect();
    }

    @Override
    public void close() {
        this.close(5000, "Bye!");
    }

    @Override
    public void close(int closeCode, String reason) {
        socket.close(closeCode, reason);
    }

    @Override
    public void dispose() {
        close();
        socket = null;
    }
}
