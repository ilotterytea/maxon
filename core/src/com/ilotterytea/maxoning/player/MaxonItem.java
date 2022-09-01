package com.ilotterytea.maxoning.player;

import com.ilotterytea.maxoning.ui.AnimatedImage;

public class MaxonItem {
    public int id;
    public String name;
    public String desc;
    public AnimatedImage icon;
    public MaxonItemEnum type;
    public float price;
    public float multiplier;

    public MaxonItem(int id, String name, String desc, AnimatedImage icon, MaxonItemEnum type, float price, float multiplier) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.icon = icon;
        this.type = type;
        this.price = price;
        this.multiplier = multiplier;
    }
}