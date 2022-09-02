package com.ilotterytea.maxoning.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.lang.StringBuilder;
import java.util.*;

public class I18N {
    private Map<String, String> language = new HashMap<>();

    public I18N(String languageId) {
        FileHandle fh = new FileHandle(String.format("i18n/%s.json", languageId));

        JsonValue json = new JsonReader().parse(fh);

        for (JsonValue val : json.iterator()) {
            language.put(val.name, json.getString(val.name));
        }
    }

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

            result.append(next);
        }

        return result.toString();
    }
}
