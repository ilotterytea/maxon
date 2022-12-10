package com.ilotterytea.maxoning.player;

import com.ilotterytea.maxoning.ui.AnimatedImage;

import java.util.ArrayList;

public class MaxonItemRegister {
    private static ArrayList<MaxonItem> items = new ArrayList<>();

    public static void register(
            int id,
            String name,
            String desc,
            AnimatedImage icon,
            MaxonItemEnum type,
            float price,
            float multiplier
    ) {
        items.add(new MaxonItem(id, name, desc, icon, type, price, multiplier));
    }

    public static void clear() { items.clear(); }

    public static void unRegister(
            int id
    ) {
        items.remove(id);
    }

    public static ArrayList<MaxonItem> getItems() { return items; }
    public static MaxonItem get(int id) {
        for (MaxonItem item : items) {
            if (item.id == id) {
                return item;
            }
        }
        return null;
    }
}
