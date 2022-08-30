package com.ilotterytea.maxoning.utils.serialization;

import com.ilotterytea.maxoning.player.MaxonPlayer;

import java.io.*;

public class GameDataSystem {
    private static final File file = new File(System.getProperty("user.home") + "/MaxoningSavegame.sav");

    public static boolean exists() { return file.exists(); }

    public static void SaveData(MaxonPlayer player) throws IOException {
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
