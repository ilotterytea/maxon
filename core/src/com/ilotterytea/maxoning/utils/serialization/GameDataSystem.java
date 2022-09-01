package com.ilotterytea.maxoning.utils.serialization;

import com.ilotterytea.maxoning.player.MaxonPlayer;
import com.ilotterytea.maxoning.utils.OsUtils;

import java.io.*;

public class GameDataSystem {
    private static final File dir = new File(OsUtils.getUserDataDirectory(".maxoning"));
    private static final File file = new File(dir.getPath() + "/savegame.sav");

    public static boolean exists() { return file.exists(); }

    public static void SaveData(MaxonPlayer player) throws IOException {
        if (!dir.exists()) {
            dir.mkdirs();
        }
        FileOutputStream fo = new FileOutputStream(file);
        ObjectOutputStream out = new ObjectOutputStream(fo);

        out.writeObject(player);
        out.close();
    }

    public static MaxonPlayer LoadData() throws IOException, ClassNotFoundException {
        FileInputStream fi = new FileInputStream(file);
        ObjectInputStream oi = new ObjectInputStream(fi);

        MaxonPlayer pl = (MaxonPlayer) oi.readObject();

        oi.close();
        fi.close();
        return pl;
    }
}
