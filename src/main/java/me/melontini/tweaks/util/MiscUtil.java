package me.melontini.tweaks.util;

import java.util.List;
import java.util.Random;

public class MiscUtil {
    public static <T> T pickRandomEntryFromList(List<T> list) {
        int random = new Random().nextInt(list.size());
        return list.get(random);
    }
}
