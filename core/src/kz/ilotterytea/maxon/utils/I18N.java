package kz.ilotterytea.maxon.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.lang.StringBuilder;
import java.util.*;

public class I18N {
    private Map<String, String> language = new HashMap<>();
    private FileHandle fileHandle;

    public I18N(FileHandle fh) {
        fileHandle = fh;

        JsonValue json = new JsonReader().parse(fileHandle);

        for (JsonValue val : json.iterator()) {
            this.language.put(val.name, json.getString(val.name));
        }
    }

    public FileHandle getFileHandle() { return fileHandle; }
    public Map<String, String> getLanguage() { return language; }

    public String TranslatableText(String id) {
        if (language.containsKey(id)) {
            return language.get(id);
        }
        return null;
    }

    public String FormattedText(String id, CharSequence... params) {
        if (!language.containsKey(id)) { return null; }
        Scanner scan = new Scanner(language.get(id));
        StringBuilder result = new StringBuilder();
        int index = 0;

        while (scan.hasNext()) {
            String next = scan.next();

            if (next.contains("%s")) {
                next = next.replace("%s", params[index]);
                if (index + 1 < params.length) { index++; }
            }

            result.append(next).append(' ');
        }

        return result.toString();
    }
}
