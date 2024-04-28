package com.ilotterytea.maxoning.screens.game.shop;

public enum ShopMultiplier {
    X1(1),
    X10(10),
    ;

    private final int multiplier;

    ShopMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    public int getMultiplier() {
        return multiplier;
    }
}
