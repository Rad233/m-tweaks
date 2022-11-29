package me.melontini.tweaks.registries;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.entity.vehicle.boats.FurnaceBoatEntity;
import me.melontini.tweaks.entity.vehicle.boats.HopperBoatEntity;
import me.melontini.tweaks.entity.vehicle.boats.JukeboxBoatEntity;
import me.melontini.tweaks.entity.vehicle.boats.TNTBoatEntity;
import me.melontini.tweaks.entity.vehicle.minecarts.AnvilMinecartEntity;
import me.melontini.tweaks.entity.vehicle.minecarts.JukeboxMinecartEntity;
import me.melontini.tweaks.entity.vehicle.minecarts.NoteBlockMinecartEntity;
import me.melontini.tweaks.util.LogUtil;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static me.melontini.tweaks.Tweaks.MODID;

@SuppressWarnings({"rawtypes", "unchecked"})
public class EntityTypeRegistry {
    public static EntityType<AnvilMinecartEntity> ANVIL_MINECART_ENTITY = createEntityType(Tweaks.CONFIG.newMinecarts.isAnvilMinecartOn, "anvil_minecart", FabricEntityTypeBuilder.<AnvilMinecartEntity>create(SpawnGroup.MISC, AnvilMinecartEntity::new).dimensions(EntityDimensions.fixed(0.98F, 0.7F)));
    public static EntityType<NoteBlockMinecartEntity> NOTEBLOCK_MINECART_ENTITY = createEntityType(Tweaks.CONFIG.newMinecarts.isNoteBlockMinecartOn, "note_block_minecart", FabricEntityTypeBuilder.<NoteBlockMinecartEntity>create(SpawnGroup.MISC, NoteBlockMinecartEntity::new).dimensions(EntityDimensions.fixed(0.98F, 0.7F)));
    public static EntityType<JukeboxMinecartEntity> JUKEBOX_MINECART_ENTITY = createEntityType(Tweaks.CONFIG.newMinecarts.isJukeboxMinecartOn, "jukebox_minecart", FabricEntityTypeBuilder.<JukeboxMinecartEntity>create(SpawnGroup.MISC, JukeboxMinecartEntity::new).dimensions(EntityDimensions.fixed(0.98F, 0.7F)));
    public static EntityType<TNTBoatEntity> BOAT_WITH_TNT = createEntityType(Tweaks.CONFIG.newBoats.isTNTBoatOn, "tnt_boat", FabricEntityTypeBuilder.<TNTBoatEntity>create(SpawnGroup.MISC, TNTBoatEntity::new).dimensions(EntityDimensions.fixed(1.375F, 0.5625F)));
    public static EntityType<FurnaceBoatEntity> BOAT_WITH_FURNACE = createEntityType(Tweaks.CONFIG.newBoats.isFurnaceBoatOn, "furnace_boat", FabricEntityTypeBuilder.<FurnaceBoatEntity>create(SpawnGroup.MISC, FurnaceBoatEntity::new).dimensions(EntityDimensions.fixed(1.375F, 0.5625F)));
    public static EntityType<JukeboxBoatEntity> BOAT_WITH_JUKEBOX = createEntityType(Tweaks.CONFIG.newBoats.isJukeboxBoatOn, "jukebox_boat", FabricEntityTypeBuilder.<JukeboxBoatEntity>create(SpawnGroup.MISC, JukeboxBoatEntity::new).dimensions(EntityDimensions.fixed(1.375F, 0.5625F)));
    public static EntityType<HopperBoatEntity> BOAT_WITH_HOPPER = createEntityType(Tweaks.CONFIG.newBoats.isHopperBoatOn, "hopper_boat", FabricEntityTypeBuilder.<HopperBoatEntity>create(SpawnGroup.MISC, HopperBoatEntity::new).dimensions(EntityDimensions.fixed(1.375F, 0.5625F)));

    public static void register() {
        LogUtil.info("EntityTypeRegistry init complete!");
    }

    public static EntityType createEntityType(String id, FabricEntityTypeBuilder builder) {
        return createEntityType(true, id, builder);
    }

    public static EntityType createEntityType(boolean shouldRegister, String id, FabricEntityTypeBuilder builder) {
        if (shouldRegister) {
            EntityType type = builder.build();
            Registry.register(Registries.ENTITY_TYPE, new Identifier(MODID, id), type);
            return type;
        }
        return null;
    }
}
