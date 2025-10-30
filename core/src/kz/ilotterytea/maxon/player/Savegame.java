package kz.ilotterytea.maxon.player;


import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.*;
import kz.ilotterytea.maxon.MaxonConstants;
import kz.ilotterytea.maxon.utils.OsUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class Savegame {
    private static FileHandle directory, file;

    private static final Logger logger = new Logger(Savegame.class.getName());

    private static Savegame data;

    private double money, multiplier;
    private final HashMap<String, Integer> purchasedPets = new HashMap<>();
    private final ArrayList<String> unlockedPets = new ArrayList<>();
    private String name;
    private long elapsedTime, slotsWins, slotsTotalSpins;
    private boolean isNewlyCreated;

    public static Savegame getInstance() {
        if (data == null) {
            data = load();
        }

        return data;
    }

    private Savegame() {
        setDefaultValues();
    }

    public static Savegame load() {
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            file = Gdx.files.absolute(Gdx.files.getExternalStoragePath() + "/savegame.maxon");
        } else {
            directory = Gdx.files.absolute(MaxonConstants.GAME_SAVEGAME_FOLDER);
            file = Gdx.files.absolute(directory.path() + "/savegame.maxon");
        }

        if (!file.exists()) {
            return new Savegame();
        }

        JsonReader reader = new JsonReader();
        JsonValue root = reader.parse(file);

        Savegame savegame = new Savegame();
        savegame.money = root.getDouble("money", 0.0f);
        savegame.multiplier = root.getDouble("multiplier", 0.0f);
        savegame.name = root.getString("name", OsUtils.isPC ? System.getProperty("user.name", "Maxon") : "Maxon");
        savegame.elapsedTime = root.getLong("elapsedTime", 0);
        savegame.slotsWins = root.getLong("slotsWins", 0);
        savegame.slotsTotalSpins = root.getLong("slotsTotalSpins", 0);
        savegame.isNewlyCreated = root.getBoolean("isNewlyCreated", false);

        JsonValue pets = root.get("purchasedPets");
        if (pets != null) {
            for (JsonValue e = pets.child; e != null; e = e.next) {
                savegame.purchasedPets.put(e.name, e.asInt());
            }
        }

        JsonValue unlockedPets = root.get("unlockedPets");
        if (unlockedPets != null) {
            for (JsonValue e = unlockedPets.child; e != null; e = e.next) {
                savegame.unlockedPets.add(e.asString());
            }
        }

        logger.info("Loaded the savegame");

        return savegame;
    }

    public void save() {
        if (OsUtils.isPC && directory != null && !directory.exists()) {
            directory.mkdirs();
        }

        StringWriter out = new StringWriter();
        JsonWriter writer = new JsonWriter(out);

        try {
            writer.object();
            writer.name("money").value(money);
            writer.name("multiplier").value(multiplier);
            writer.name("name").value(name);
            writer.name("elapsedTime").value(elapsedTime);
            writer.name("slotsWins").value(slotsWins);
            writer.name("slotsTotalSpins").value(slotsTotalSpins);
            writer.name("isNewlyCreated").value(isNewlyCreated);

            writer.name("purchasedPets");
            writer.object();
            for (var e : purchasedPets.entrySet()) {
                writer.name(e.getKey()).value(e.getValue());
            }
            writer.pop();

            writer.name("unlockedPets");
            writer.array();
            for (String s : unlockedPets) {
                writer.value(s);
            }
            writer.pop();

            writer.pop();
            writer.close();
        } catch (IOException e) {
            logger.error("Failed to serialize the savegame", e);
            return;
        }

        try {
            file.writeString(out.toString(), false);
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
