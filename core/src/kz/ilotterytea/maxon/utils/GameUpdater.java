package kz.ilotterytea.maxon.utils;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Objects;


public class GameUpdater {
    static class Release {
        String url;
        String html_url;
        String assets_url;
        String upload_url;
        String tarball_url;
        String zipball_url;
        String discussion_url;
        int id;
        String node_id;
        String tag_name;
        String target_commitish;
        String name;
        String body;
        boolean draft;
        boolean prerelease;
        String created_at;
        String published_at;
        Author author;
        Assets[] assets;
    }

    static class Author {
        String login;
        int id;
        String node_id;
        String avatar_url;
        String gravatar_id;
        String url;
        String html_url;
        String followers_url;
        String following_url;
        String gists_url;
        String starred_url;
        String subscriptions_url;
        String organizations_url;
        String repos_url;
        String events_url;
        String received_events_url;
        String type;
        boolean site_admin;
    }

    static class Assets {
        String url;
        String browser_download_url;
        int id;
        String node_id;
        String name;
        String label;
        String state;
        String content_type;
        int size;
        int download_count;
        String created_at;
        String updated_at;
        Uploader uploader;
    }

    static class Uploader {
        String login;
        int id;
        String node_id;
        String avatar_url;
        String gravatar_id;
        String url;
        String html_url;
        String followers_url;
        String following_url;
        String gists_url;
        String starred_url;
        String subscriptions_url;
        String organizations_url;
        String repos_url;
        String events_url;
        String received_events_url;
        String type;
        boolean site_admin;
    }

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    public static boolean isLatestRelease(String githubTag) throws Exception {
        return Objects.equals(githubTag, getLatestRelease().tag_name);
    }

    public static String getLatestVersion() throws Exception {
        return getLatestRelease().tag_name;
    }

    public static Release getLatestRelease() throws Exception {
        String url_link = "https://api.github.com/repos/NotDankEnough/MaxonPettingSim/releases/latest";

        String json = readUrl(url_link);

        Gson gson = new Gson();

        return gson.fromJson(json, Release.class);
    }
}
