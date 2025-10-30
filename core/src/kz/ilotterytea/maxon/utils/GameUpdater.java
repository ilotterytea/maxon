package kz.ilotterytea.maxon.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Logger;
import kz.ilotterytea.maxon.MaxonConstants;

import java.util.ArrayList;

public class GameUpdater implements Net.HttpResponseListener {
    private final Logger logger = new Logger(GameUpdater.class.getName());
    public static boolean CLIENT_IS_ON_LATEST_VERSION;

    public void checkLatestUpdate() {
        Net.HttpRequest request =
                new HttpRequestBuilder()
                        .newRequest()
                        .method(Net.HttpMethods.GET)
                        .url(MaxonConstants.GAME_VERSIONS_FILE_URL)
                        .timeout(5000)
                        .build();

        Gdx.net.sendHttpRequest(request, this);
    }

    @Override
    public void handleHttpResponse(Net.HttpResponse httpResponse) {
        String response = httpResponse.getResultAsString();

        if (response == null) {
            logger.error("Got null in response");
            CLIENT_IS_ON_LATEST_VERSION = true;
            return;
        }

        ArrayList versions = new Json().fromJson(ArrayList.class, response);

        try {
            GameVersion latestVersion = (GameVersion) versions.get(0);
            CLIENT_IS_ON_LATEST_VERSION = latestVersion.getVersion().equals(MaxonConstants.GAME_VERSION);
        } catch (Exception e) {
            logger.error("Failed to find the latest version");
            CLIENT_IS_ON_LATEST_VERSION = true;
        }
    }

    @Override
    public void failed(Throwable t) {
        logger.error(t.getMessage());
        CLIENT_IS_ON_LATEST_VERSION = true;
    }

    @Override
    public void cancelled() {
        logger.info("Cancelled");
        CLIENT_IS_ON_LATEST_VERSION = true;
    }

    private static class GameVersion {
        private String version;

        public String getVersion() {
            return version;
        }

    }
}
