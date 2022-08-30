package com.ilotterytea.maxoning.player;

import com.ilotterytea.maxoning.ui.AnimatedImage;

public class MaxonItem {
    public String name;
    public String desc;
    public AnimatedImage icon;
    public MaxonItemEnum type;
    public float price;

    public MaxonItem(String name, String desc, AnimatedImage icon, MaxonItemEnum type, float price) {
        this.name = name;
        this.desc = desc;
        this.icon = icon;
        this.type = type;
        this.price = price;
    }
}