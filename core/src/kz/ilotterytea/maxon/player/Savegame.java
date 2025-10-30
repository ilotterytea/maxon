package kz.ilotterytea.maxon.player;


import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.SerializationException;
import kz.ilotterytea.maxon.MaxonConstants;
import kz.ilotterytea.maxon.utils.OsUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Savegame implements Serializable {
    private static final FileHandle directory = Gdx.files.absolute(MaxonConstants.GAME_SAVEGAME_FOLDER);
    private static FileHandle file;

    private static final Logger logger = new Logger(Savegame.class.getName());

    private double money, multiplier;
    private final HashMap<String, Integer> purchasedPets = new HashMap<>();
    private final ArrayList<String> unlockedPets = new ArrayList<>();
    private String name;
    private long elapsedTime, slotsWins, slotsTotalSpins;
    private boolean isNewlyCreated;

    private static Savegame savegame;

    public static Savegame getInstance() {
        if (savegame == null) {
            savegame = load();
        }

        return savegame;
    }

    private Savegame() {
        setDefaultValues();
    }

    public static Savegame load() {
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            file = Gdx.files.absolute(Gdx.files.getExternalStoragePath() + "/savegame.maxon");
        } else {
            file = Gdx.files.absolute(directory.path() + "/savegame.maxon");
        }

        if (!file.exists()) {
            return new Savegame();
        }

        Savegame savegame = null;

        try {
            savegame = new Json().fromJson(Savegame.class, file);
        } catch (SerializationException e) {
            logger.error("Failed to parse the savegame", e);
            file.delete();
        }

        if (savegame == null) {
            logger.error("Failed to load a save");
            return new Savegame();
        }

        savegame.isNewlyCreated = false;

        logger.info("Loaded the savegame");

        return savegame;
    }

    public void save() {
        if (OsUtils.isPC && !directory.exists()) {
            directory.mkdirs();
        }

        try {
            file.writeString(new Json().toJson(this), false);
            logger.info("Saved the game");
        } catch (GdxRuntimeException e) {
            logger.error("Failed to save the game", e);
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
        unlockedPets.clear();
        name = OsUtils.isPC ? System.getProperty("user.name", "Maxon") : "Maxon";
        elapsedTime = 0;
        slotsWins = 0;
        slotsTotalSpins = 0;
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

    public void increaseMultiplier(double multiplier) {
        this.multiplier += multiplier;
    }

    public void decreaseMultiplier(double multiplier) {
        this.multiplier -= multiplier;
    }

    public HashMap<String, Integer> getPurchasedPets() {
        return purchasedPets;
    }

    public Integer getAllPetAmount() {
        Integer sum = 0;

        for (Integer v : getPurchasedPets().values()) {
            sum += v;
        }

        return sum;
    }

    public ArrayList<String> getUnlockedPets() {
        return unlockedPets;
    }

    public String getName() {
        return name;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public long getSlotsTotalSpins() {
        return slotsTotalSpins;
    }

    public void setSlotsTotalSpins(long slotsTotalSpins) {
        this.slotsTotalSpins = slotsTotalSpins;
    }

    public long getSlotsWins() {
        return slotsWins;
    }

    public void setSlotsWins(long slotsWins) {
        this.slotsWins = slotsWins;
    }

    public boolean isNewlyCreated() {
        return isNewlyCreated;
    }

    public void setNewlyCreated(boolean newlyCreated) {
        isNewlyCreated = newlyCreated;
    }
}
