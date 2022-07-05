package me.melontini.tweaks.registries;

import me.melontini.tweaks.config.TweaksConfig;
import me.melontini.tweaks.entity.vehicle.boats.FurnaceBoatEntity;
import me.melontini.tweaks.entity.vehicle.boats.JukeboxBoatEntity;
import me.melontini.tweaks.entity.vehicle.boats.TNTBoatEntity;
import me.melontini.tweaks.entity.vehicle.minecarts.AnvilMinecartEntity;
import me.melontini.tweaks.entity.vehicle.minecarts.JukeboxMinecartEntity;
import me.melontini.tweaks.entity.vehicle.minecarts.NoteBlockMinecartEntity;
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
    public static EntityType<TNTBoatEntity> BOAT_WITH_TNT;
    public static EntityType<FurnaceBoatEntity> BOAT_WITH_FURNACE;
    public static EntityType<AnvilMinecartEntity> ANVIL_MINECART_ENTITY;

    public static EntityType<NoteBlockMinecartEntity> NOTEBLOCK_MINECART_ENTITY;

    public static EntityType<JukeboxMinecartEntity> JUKEBOX_MINECART_ENTITY;

    public static EntityType<JukeboxBoatEntity> BOAT_WITH_JUKEBOX;

    public static void register() {
        TweaksConfig config = AutoConfig.getConfigHolder(TweaksConfig.class).getConfig();
        if (config.newMinecarts.isAnvilMinecartOn) {
            ANVIL_MINECART_ENTITY = FabricEntityTypeBuilder.<AnvilMinecartEntity>create(SpawnGroup.MISC, AnvilMinecartEntity::new)
                    .dimensions(EntityDimensions.fixed(0.98F, 0.7F))
                    .trackRangeBlocks(8)
                    .build();
            Registry.register(Registry.ENTITY_TYPE, new Identifier(MODID, "anvil_minecart"), ANVIL_MINECART_ENTITY);
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

        if (config.newBoats.isFurnaceBoatOn) {
            BOAT_WITH_FURNACE = FabricEntityTypeBuilder.<FurnaceBoatEntity>create(SpawnGroup.MISC, FurnaceBoatEntity::new)
                    .dimensions(EntityDimensions.fixed(1.375F, 0.5625F))
                    .trackRangeBlocks(10)
                    .build();
            Registry.register(Registry.ENTITY_TYPE, new Identifier(MODID, "furnace_boat"), BOAT_WITH_FURNACE);
        }

        if (config.newBoats.isJukeboxBoatOn) {
            BOAT_WITH_JUKEBOX = FabricEntityTypeBuilder.<JukeboxBoatEntity>create(SpawnGroup.MISC, JukeboxBoatEntity::new)
                    .dimensions(EntityDimensions.fixed(1.375F, 0.5625F))
                    .trackRangeBlocks(10)
                    .build();
            Registry.register(Registry.ENTITY_TYPE, new Identifier(MODID, "jukebox_boat"), BOAT_WITH_JUKEBOX);
        }

        if (config.newBoats.isTNTBoatOn) {
            BOAT_WITH_TNT = FabricEntityTypeBuilder.<TNTBoatEntity>create(SpawnGroup.MISC, TNTBoatEntity::new)
                    .dimensions(EntityDimensions.fixed(1.375F, 0.5625F))
                    .trackRangeBlocks(10)
                    .build();
            Registry.register(Registry.ENTITY_TYPE, new Identifier(MODID, "tnt_boat"), BOAT_WITH_TNT);
        }

        LogUtil.info("EntityTypeRegistry init complete!");
    }
}
