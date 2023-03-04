package me.melontini.tweaks.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TweaksTexts {
    public static final MutableText MINECART_LINK_WHAT = generic("m-tweaks.simpleMinecartLinking.what");
    public static final MutableText MINECART_LINK_TOO_FAR = generic("m-tweaks.simpleMinecartLinking.too_far");
    public static final MutableText MINECART_LINK_SELF = generic("m-tweaks.simpleMinecartLinking.link_self");
    public static final MutableText MINECART_LINK_DE_SYNC = generic("m-tweaks.simpleMinecartLinking.de_sync");
    public static final Text FLETCHING_SCREEN = generic("gui.m-tweaks.fletching");
    public static final Text SAFE_BEDS = generic("m-tweaks.safebeds.action");
    public static final Text INCUBATOR_0 = generic("tooltip.m-tweaks.incubator[0]");
    public static final Text INCUBATOR_1 = genericGray("tooltip.m-tweaks.incubator[1]");
    public static final Text ITEM_IN_FRAME = genericGray("tooltip.m-tweaks.frameitem");
    public static final Text ROSE_OF_THE_VALLEY_TOOLTIP = genericGray("tooltip.m-tweaks.rose_of_the_valley");

    public static MutableText genericGray(String key, Object... args) {
        return Text.translatable(key, args).formatted(Formatting.GRAY);
    }

    public static MutableText genericGray(String key) {
        return Text.translatable(key).formatted(Formatting.GRAY);
    }

    public static MutableText generic(String key, Object... args) {
        return Text.translatable(key, args);
    }

    public static MutableText generic(String key) {
        return Text.translatable(key);
    }

    public static MutableText literal(String text) {
        return Text.literal(text);
    }
}
