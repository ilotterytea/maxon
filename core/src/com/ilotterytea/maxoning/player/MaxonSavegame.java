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
    public int points;
    /** Multiplier. */
    public short multiplier;

    /** Home inventory. */
    public ArrayList<Integer> inv;
    /** Outside Inventory. */
    public ArrayList<Integer> outInv;

    /** Seed. */
    public long seed;

    /** Player name. */
    public String name;
    /** Pet name. */
    public String petName;
    /** Pet ID. */
    public byte petId;

    /** Elapsed time from game start. */
    public long elapsedTime;

    /** Last timestamp when save game was used. */
    public long lastTimestamp;

    /** Location. */
    public short roomId;
}