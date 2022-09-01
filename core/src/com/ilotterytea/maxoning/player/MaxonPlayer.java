package com.ilotterytea.maxoning.player;

import java.io.Serializable;
import java.util.ArrayList;

public class MaxonPlayer implements Serializable {
    public float points;
    public float multiplier;
    public ArrayList<Integer> purchasedItems;

    public MaxonPlayer() {
        this.points = 0;
        this.multiplier = 1.2f;
        this.purchasedItems = new ArrayList<>();
    }

    public void load(MaxonPlayer player) {
        if (player != null) {
            this.points = player.points;
            this.multiplier = player.multiplier;

            this.purchasedItems.clear();
            this.purchasedItems.addAll(player.purchasedItems);
        }
    }
}
