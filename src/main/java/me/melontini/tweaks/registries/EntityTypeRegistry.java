package me.melontini.tweaks.registries;

import me.melontini.crackerutil.content.RegistryUtil;
import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.entity.FlyingItemEntity;
import me.melontini.tweaks.entity.vehicle.boats.FurnaceBoatEntity;
import me.melontini.tweaks.entity.vehicle.boats.HopperBoatEntity;
import me.melontini.tweaks.entity.vehicle.boats.JukeboxBoatEntity;
import me.melontini.tweaks.entity.vehicle.boats.TNTBoatEntity;
import me.melontini.tweaks.entity.vehicle.minecarts.AnvilMinecartEntity;
import me.melontini.tweaks.entity.vehicle.minecarts.JukeboxMinecartEntity;
import me.melontini.tweaks.entity.vehicle.minecarts.NoteBlockMinecartEntity;
import me.melontini.tweaks.util.TweaksLog;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;

import static me.melontini.tweaks.Tweaks.MODID;

public class EntityTypeRegistry {
    public static EntityType<AnvilMinecartEntity> ANVIL_MINECART_ENTITY = RegistryUtil.createEntityType(Tweaks.CONFIG.newMinecarts.isAnvilMinecartOn, new Identifier(MODID, "anvil_minecart"), EntityType.Builder.<AnvilMinecartEntity>create(AnvilMinecartEntity::new, SpawnGroup.MISC).setDimensions(0.98F, 0.7F));
    public static EntityType<NoteBlockMinecartEntity> NOTEBLOCK_MINECART_ENTITY = RegistryUtil.createEntityType(Tweaks.CONFIG.newMinecarts.isNoteBlockMinecartOn, new Identifier(MODID, "note_block_minecart"), EntityType.Builder.<NoteBlockMinecartEntity>create(NoteBlockMinecartEntity::new, SpawnGroup.MISC).setDimensions(0.98F, 0.7F));
    public static EntityType<JukeboxMinecartEntity> JUKEBOX_MINECART_ENTITY = RegistryUtil.createEntityType(Tweaks.CONFIG.newMinecarts.isJukeboxMinecartOn, new Identifier(MODID, "jukebox_minecart"), EntityType.Builder.<JukeboxMinecartEntity>create(JukeboxMinecartEntity::new, SpawnGroup.MISC).setDimensions(0.98F, 0.7F));
    public static EntityType<TNTBoatEntity> BOAT_WITH_TNT = RegistryUtil.createEntityType(Tweaks.CONFIG.newBoats.isTNTBoatOn, new Identifier(MODID, "tnt_boat"), EntityType.Builder.<TNTBoatEntity>create(TNTBoatEntity::new, SpawnGroup.MISC).setDimensions(1.375F, 0.5625F));
    public static EntityType<FurnaceBoatEntity> BOAT_WITH_FURNACE = RegistryUtil.createEntityType(Tweaks.CONFIG.newBoats.isFurnaceBoatOn, new Identifier(MODID, "furnace_boat"), EntityType.Builder.<FurnaceBoatEntity>create(FurnaceBoatEntity::new, SpawnGroup.MISC).setDimensions(1.375F, 0.5625F));
    public static EntityType<JukeboxBoatEntity> BOAT_WITH_JUKEBOX = RegistryUtil.createEntityType(Tweaks.CONFIG.newBoats.isJukeboxBoatOn, new Identifier(MODID, "jukebox_boat"), EntityType.Builder.<JukeboxBoatEntity>create(JukeboxBoatEntity::new, SpawnGroup.MISC).setDimensions(1.375F, 0.5625F));
    public static EntityType<HopperBoatEntity> BOAT_WITH_HOPPER = RegistryUtil.createEntityType(Tweaks.CONFIG.newBoats.isHopperBoatOn, new Identifier(MODID, "hopper_boat"), EntityType.Builder.<HopperBoatEntity>create(HopperBoatEntity::new, SpawnGroup.MISC).setDimensions(1.375F, 0.5625F));
    public static EntityType<FlyingItemEntity> FLYING_ITEM = RegistryUtil.createEntityType(Tweaks.CONFIG.newBoats.isHopperBoatOn, new Identifier(MODID, "flying_item"), EntityType.Builder.<FlyingItemEntity>create(FlyingItemEntity::new, SpawnGroup.MISC)
            .setDimensions(0.25F, 0.25F).maxTrackingRange(4).trackingTickInterval(10));

    public static void register() {
        TweaksLog.info("EntityTypeRegistry init complete!");
    }


}
