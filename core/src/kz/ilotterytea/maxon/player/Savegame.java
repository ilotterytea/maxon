package kz.ilotterytea.maxon.player;


import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import kz.ilotterytea.maxon.MaxonConstants;
import kz.ilotterytea.maxon.utils.OsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;

public class Savegame implements Serializable {
    private static final File directory = new File(MaxonConstants.GAME_SAVEGAME_FOLDER);
    private static final File file = new File(
            String.format(
                    "%s/savegame.maxon",
                    (OsUtils.isAndroid || OsUtils.isIos)
                            ? Gdx.files.getExternalStoragePath()
                            : directory.getAbsolutePath()
            )
    );
    private static final Gson gson = new Gson();
    private static final Logger logger = LoggerFactory.getLogger(Savegame.class);

    private double money, multiplier;
    private final HashMap<String, Integer> purchasedPets = new HashMap<>();
    private String name;
    private long elapsedTime;
    private boolean isNewlyCreated;

    private Savegame() {
        setDefaultValues();
    }

    public static Savegame load() {
        if (!file.exists()) {
            return new Savegame();
        }

        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);

            Savegame savegame = gson.fromJson(ois.readUTF(), Savegame.class);
            ois.close();

            savegame.isNewlyCreated = false;

            logger.info("Loaded the savegame");

            return savegame;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load savegame", e);
        }
    }

    public void save() {
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeUTF(gson.toJson(this));
            oos.close();

            logger.info("Saved the game");
        } catch (IOException e) {
            throw new RuntimeException("Failed to save the game", e);
        }
    }

    public void delete() {
        if (file.delete()) {
            setDefaultValues();
        }
    }

    private void setDefaultValues() {
        money = 0.0f;
        multiplier = 0.0f;
        purchasedPets.clear();
        name = System.getProperty("user.name", "Maxon");
        elapsedTime = 0;
        isNewlyCreated = true;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public void increaseMoney(double money) {
        this.money += money;
    }

    public void decreaseMoney(double money) {
        this.money -= money;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public void increaseMultiplier(double multiplier) {
        this.multiplier += multiplier;
    }

    public void decreaseMultiplier(double multiplier) {
        this.multiplier -= multiplier;
    }

    public HashMap<String, Integer> getPurchasedPets() {
        return purchasedPets;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public boolean isNewlyCreated() {
        return isNewlyCreated;
    }
}
