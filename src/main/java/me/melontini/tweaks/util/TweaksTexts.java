package me.melontini.tweaks.util;

import me.melontini.crackerutil.util.TextUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TweaksTexts {
    public static final Text ITEM_GROUP_NAME = TextUtil.translatable("itemGroup.m_tweaks.items");
    public static final MutableText MINECART_LINK_WHAT = TextUtil.translatable("m-tweaks.simpleMinecartLinking.what");
    public static final MutableText MINECART_LINK_TOO_FAR = TextUtil.translatable("m-tweaks.simpleMinecartLinking.too_far");
    public static final MutableText MINECART_LINK_SELF = TextUtil.translatable("m-tweaks.simpleMinecartLinking.link_self");
    public static final MutableText MINECART_LINK_DE_SYNC = TextUtil.translatable("m-tweaks.simpleMinecartLinking.de_sync");
    public static final Text FLETCHING_SCREEN = TextUtil.translatable("gui.m-tweaks.fletching");
    public static final Text SAFE_BEDS = TextUtil.translatable("m-tweaks.safebeds.action");
    public static final Text INCUBATOR_0 = TextUtil.translatable("tooltip.m-tweaks.incubator[0]");
    public static final Text INCUBATOR_1 = TextUtil.translatable("tooltip.m-tweaks.incubator[1]").formatted(Formatting.GRAY);
    public static final Text ITEM_IN_FRAME = TextUtil.translatable("tooltip.m-tweaks.frameitem").formatted(Formatting.GRAY);
    public static final Text ROSE_OF_THE_VALLEY_TOOLTIP = TextUtil.translatable("tooltip.m-tweaks.rose_of_the_valley").formatted(Formatting.GRAY);
}
