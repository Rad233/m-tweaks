package me.melontini.tweaks.registries;

import me.melontini.tweaks.config.TweaksConfig;
import me.melontini.tweaks.entity.vehicle.AnvilMinecartEntity;
import me.melontini.tweaks.entity.vehicle.JukeboxMinecartEntity;
import me.melontini.tweaks.entity.vehicle.NoteBlockMinecartEntity;
import me.melontini.tweaks.util.LogUtil;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static me.melontini.tweaks.Tweaks.MODID;

public class EntityTypeRegistry {
    public static EntityType<AnvilMinecartEntity> ANVIL_MINECART_ENTITY;

    public static EntityType<NoteBlockMinecartEntity> NOTEBLOCK_MINECART_ENTITY;

    public static EntityType<JukeboxMinecartEntity> JUKEBOX_MINECART_ENTITY;

    public static void register() {
        TweaksConfig config = AutoConfig.getConfigHolder(TweaksConfig.class).getConfig();
        if (config.newMinecarts.isAnvilMinecartOn) {
            ANVIL_MINECART_ENTITY = FabricEntityTypeBuilder.<AnvilMinecartEntity>create(SpawnGroup.MISC, AnvilMinecartEntity::new)
                    .dimensions(EntityDimensions.fixed(0.98F, 0.7F))
                    .trackRangeBlocks(8)
                    .build();
            Registry.register(Registry.ENTITY_TYPE, new Identifier(MODID, "anvil_minecart.json"), ANVIL_MINECART_ENTITY);
        }
        if (config.newMinecarts.isNoteBlockMinecartOn) {
            NOTEBLOCK_MINECART_ENTITY = FabricEntityTypeBuilder.<NoteBlockMinecartEntity>create(SpawnGroup.MISC, NoteBlockMinecartEntity::new)
                    .dimensions(EntityDimensions.fixed(0.98F, 0.7F))
                    .trackRangeBlocks(8)
                    .build();
            Registry.register(Registry.ENTITY_TYPE, new Identifier(MODID, "note_block_minecart"), NOTEBLOCK_MINECART_ENTITY);
        }
        if (config.newMinecarts.isJukeboxMinecartOn) {
            JUKEBOX_MINECART_ENTITY = FabricEntityTypeBuilder.<JukeboxMinecartEntity>create(SpawnGroup.MISC, JukeboxMinecartEntity::new)
                    .dimensions(EntityDimensions.fixed(0.98F, 0.7F))
                    .trackRangeBlocks(8)
                    .build();
            Registry.register(Registry.ENTITY_TYPE, new Identifier(MODID, "jukebox_minecart"), JUKEBOX_MINECART_ENTITY);
        }

        LogUtil.info("EntityTypeRegistry init complete!");
    }
}
