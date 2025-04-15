package com.example.dicerealmandroid.Color_hashmap;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Colorhashmap {
    List<UUID> uuidList = new ArrayList<>();

    public String getColor(UUID uuid) {
        if (!uuidList.contains(uuid)) {
            uuidList.add(uuid);
        }

        return generateColor(Integer.toString(uuidList.indexOf(uuid)));
    }

    public static String generateColor(String uuid) {
        Log.d("COLORUUID",uuid);
        switch (uuid) {
            case "1": return "🟧"; // orange
            case "2": return "🟨"; // yellow
            case "3": return "🟩"; // green
            case "4": return "🟦"; // blue
            case "5": return "🟪"; // purple
            case "0": return "⬛"; // black-ish
            default: return "⬜"; // fallback
        }
    }
}
