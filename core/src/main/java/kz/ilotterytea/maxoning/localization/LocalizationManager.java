package kz.ilotterytea.maxoning.localization;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.HashMap;

public class LocalizationManager {
    private final HashMap<String, HashMap<LineId, String>> LOADED_LOCALIZATIONS;

    public LocalizationManager() {
        this.LOADED_LOCALIZATIONS = new HashMap<>();
    }

    public String literalText(String localizationId, LineId lineId) {
        if (!this.LOADED_LOCALIZATIONS.containsKey(localizationId)) {
            return "LocaleNotFound";
        }

        return this.LOADED_LOCALIZATIONS.get(localizationId).get(lineId);
    }

    public String formatText(String localizationId, LineId lineId, String... parameters) {
        String literalText = literalText(localizationId, lineId);
        String[] splitLiteralText = literalText.split(" ");
        StringBuilder stringBuilder = new StringBuilder();
        int index = 0;

        for (String word : splitLiteralText) {
            if (word.contains("%s")) {
                word = word.replace("%s", parameters[index]);
            }

            if (index + 1 < parameters.length - 1) {
                index++;
            }

            stringBuilder.append(word);
        }

        return stringBuilder.toString();
    }

    public void loadLocalizations(String directoryPath) {
        FileHandle directory = Gdx.files.internal(directoryPath);

        if (!directory.exists()) {
            Gdx.app.error(LocalizationManager.class.getName(), "Directory \"" + directoryPath + "\" not exists!");
            return;
        }

        if (!directory.isDirectory()) {
            Gdx.app.error(LocalizationManager.class.getName(), "Path \"" + directoryPath + "\" is not a directory! This method can apply only directories.");
            return;
        }

        for (FileHandle fileHandle : directory.list(".json")) {
            loadLocalization(fileHandle);
        }
    }

    private void loadLocalization(FileHandle fileHandle) {
        String localeId = fileHandle.nameWithoutExtension();
        HashMap<LineId, String> map = new HashMap<>();

        JsonValue jsonFile = new JsonReader().parse(fileHandle.reader());

        for (LineId lineId : LineId.values()) {
            map.put(lineId, jsonFile.getString(lineId.getName(), "LineNotFound"));
        }

        this.LOADED_LOCALIZATIONS.put(localeId, map);
    }

    public HashMap<String, HashMap<LineId, String>> getLoadedLocalizations() {
        return LOADED_LOCALIZATIONS;
    }
}
