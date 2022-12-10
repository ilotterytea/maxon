package com.ilotterytea.maxoning.player;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Save-game data class.
 * @author NotDankEnough
 * @since Alpha 1.2 (Oct 02, 2022)
 */
public class MaxonSavegame implements Serializable {
    /** Earned Squish Points. */
    public int points = 0;
    /** Multiplier. */
    public int multiplier = 1;

    /** Home inventory. */
    public ArrayList<Integer> inv = new ArrayList<>();
    /** Outside Inventory. */
    public ArrayList<Integer> outInv = new ArrayList<>();

    /** Seed. */
    public long seed = System.currentTimeMillis();

    /** Player name. */
    public String name = System.getProperty("user.name");
    /** Pet name. */
    public String petName = "Maxon";
    /** Pet ID. */
    public byte petId = 0;

    /** Elapsed time from game start. */
    public long elapsedTime = 0;

    /** Last timestamp when save game was used. */
    public long lastTimestamp = System.currentTimeMillis();

    /** Location. */
    public short roomId = 0;
}