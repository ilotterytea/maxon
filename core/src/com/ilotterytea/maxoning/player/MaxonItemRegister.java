package com.ilotterytea.maxoning.player;

import com.ilotterytea.maxoning.ui.AnimatedImage;

import java.util.HashMap;
import java.util.Map;

public class MaxonItemRegister {
    private static Map<Integer, MaxonItem> items = new HashMap<>();

    public static void register(
            int id,
            String name,
            String desc,
            AnimatedImage icon,
            MaxonItemEnum type,
            float price
    ) {
        items.put(id, new MaxonItem(name, desc, icon, type, price));
    }

    public static void unRegister(
            int id
    ) {
        items.remove(id);
    }

    public static Map<Integer, MaxonItem> getItems() { return items; }
    public static MaxonItem get(int id) { return items.get(id); }
    public static boolean contains(int id) { return items.containsKey(id); }
}
