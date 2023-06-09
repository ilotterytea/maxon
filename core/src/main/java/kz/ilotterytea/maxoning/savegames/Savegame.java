package kz.ilotterytea.maxoning.savegames;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

public class Savegame implements Serializable {
    private int level;
    private double points;
    private float multiplier;

    private ArrayList<Integer> purchasedItemIds;

    private final String playerName;
    private int playerSkinId;

    private int backgroundId;
    private int backgroundFurnitureId;

    private float fatStatus;
    private float hungerStatus;
    private float happyStatus;
    private float sleepStatus;

    private double totalTimePlayed;
    private double lastTimePlayed;
    private final double firstTimePlayed;

    public Savegame() {
        this.level = 1;
        this.points = 0f;
        this.multiplier = 0.1f;
        this.purchasedItemIds = new ArrayList<>();
        this.playerName = System.getProperty("java.home");
        this.playerSkinId = 0;
        this.backgroundId = 0;
        this.backgroundFurnitureId = 0;
        this.fatStatus = 0f;
        this.hungerStatus = 0f;
        this.happyStatus = 100f;
        this.sleepStatus = 0f;
        this.totalTimePlayed = 0f;
        this.firstTimePlayed = new Date().getTime();
        this.lastTimePlayed = new Date().getTime();
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }

    public ArrayList<Integer> getPurchasedItemIds() {
        return purchasedItemIds;
    }

    public void setPurchasedItemIds(ArrayList<Integer> purchasedItemIds) {
        this.purchasedItemIds = purchasedItemIds;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getPlayerSkinId() {
        return playerSkinId;
    }

    public void setPlayerSkinId(int playerSkinId) {
        this.playerSkinId = playerSkinId;
    }

    public int getBackgroundId() {
        return backgroundId;
    }

    public void setBackgroundId(int backgroundId) {
        this.backgroundId = backgroundId;
    }

    public int getBackgroundFurnitureId() {
        return backgroundFurnitureId;
    }

    public void setBackgroundFurnitureId(int backgroundFurnitureId) {
        this.backgroundFurnitureId = backgroundFurnitureId;
    }

    public float getFatStatus() {
        return fatStatus;
    }

    public void setFatStatus(float fatStatus) {
        this.fatStatus = fatStatus;
    }

    public float getHungerStatus() {
        return hungerStatus;
    }

    public void setHungerStatus(float hungerStatus) {
        this.hungerStatus = hungerStatus;
    }

    public float getHappyStatus() {
        return happyStatus;
    }

    public void setHappyStatus(float happyStatus) {
        this.happyStatus = happyStatus;
    }

    public float getSleepStatus() {
        return sleepStatus;
    }

    public void setSleepStatus(float sleepStatus) {
        this.sleepStatus = sleepStatus;
    }

    public double getTotalTimePlayed() {
        return totalTimePlayed;
    }

    public void setTotalTimePlayed(double totalTimePlayed) {
        this.totalTimePlayed = totalTimePlayed;
    }

    public double getLastTimePlayed() {
        return lastTimePlayed;
    }

    public void setLastTimePlayed(double lastTimePlayed) {
        this.lastTimePlayed = lastTimePlayed;
    }

    public double getFirstTimePlayed() {
        return firstTimePlayed;
    }

    public void saveToFile(String fileName) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(this);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<Savegame> loadFromFile(String fileName) {
        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Savegame savegame = (Savegame) objectInputStream.readObject();

            objectInputStream.close();
            fileInputStream.close();
            return Optional.ofNullable(savegame);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
