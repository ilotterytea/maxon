package kz.ilotterytea.maxon.localization;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.StringBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LocalizationManager {
    private final Map<LineId, String> lines = new HashMap<>();
    private final FileHandle handle;

    public LocalizationManager(FileHandle localizationFile) {
        this.handle = localizationFile;

        JsonValue json = new JsonReader().parse(handle);
        for (JsonValue val : json.iterator()) {
            LineId key = LineId.fromJson(val.name);
            if (key == null) continue;
            String value;

            try {
                value = json.getString(val.name);
            } catch (Exception e) {
                value = val.name;
            }

            lines.put(key, value);
        }
    }

    public String getLine(LineId id) {
        return lines.get(id);
    }

    public String getFormattedLine(LineId id, CharSequence... params) {
        String line = this.getLine(id);

        if (line == null) return null;

        Scanner scanner = new Scanner(line);
        StringBuilder result = new StringBuilder();
        int index = 0;

        while (scanner.hasNext()) {
            String next = scanner.next();

            if (next.contains("%s")) {
                next = next.replace("%s", params[index]);

                if (index + 1 < params.length) index++;
            }

            result.append(next).append(' ');
        }

        return result.substring(0, result.length - 1);
    }

    public FileHandle getHandle() {
        return handle;
    }
}
