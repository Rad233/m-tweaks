package me.melontini.tweaks;

import com.chocohead.mm.api.ClassTinkerers;
import me.melontini.tweaks.config.TweaksConfig;
import me.melontini.tweaks.util.LogUtil;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

public class TweaksEarlyRiser implements Runnable{
    @Override
    public void run() {
        //lol
        AutoConfig.register(TweaksConfig.class, JanksonConfigSerializer::new);

        final MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();
        final String minecartTypes = mappingResolver.mapClassName("intermediary", "net.minecraft.class_1688$class_1689");

        //new minecarts
        ClassTinkerers.enumBuilder(minecartTypes)
                .addEnum("M_TWEAKS_ANVIL")
                .addEnum("M_TWEAKS_NOTEBLOCK")
                .addEnum("M_TWEAKS_JUKEBOX")
                .build();

        LogUtil.importantInfo("Definitely up to a lot of good");
    }
}
