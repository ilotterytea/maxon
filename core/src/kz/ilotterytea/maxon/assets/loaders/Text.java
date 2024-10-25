package kz.ilotterytea.maxon.assets.loaders;

import com.badlogic.gdx.files.FileHandle;

public class Text {
    private final String string;

    public Text(FileHandle file) {
        this.string = new String(file.readBytes());
    }

    public String getString() {
        return this.string;
    }

}