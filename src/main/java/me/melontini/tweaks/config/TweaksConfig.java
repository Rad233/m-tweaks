package me.melontini.tweaks.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"ArraysAsListWithZeroOrOneArgument"})
@Config(name = "m-tweaks")
@Config.Gui.Background("minecraft:textures/block/dirt.png")
public class TweaksConfig implements ConfigData {
    @ConfigEntry.Category("world")
    @Comment("Pretty self-descriptive. Makes Bee Nests fall after some time, you can also shoot them with an arrow")
    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean canBeeNestsFall = true;

    @ConfigEntry.Category("blocks")
    @Comment("Makes beds explode in every dimension. conflicts with Safe Beds!")
    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean bedsExplodeEverywhere = false;

    @ConfigEntry.Category("blocks")
    @Comment("Configurable bed explosion power. default 5.0")
    @ConfigEntry.Gui.Tooltip
    public float bedExplosionPower = 5.0F;

    @ConfigEntry.Category("blocks")
    @Comment("Makes beds not explode when outside the overworld. conflicts with Beds Explode Everywhere!")
    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean safeBeds = false;

    @ConfigEntry.Category("blocks")
    @Comment("If on, entities standing on leaves will have their movement speed hindered")
    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean leafSlowdown = false;

    @ConfigEntry.Category("blocks")
    @Comment("With this rule on, crops will grow slower in colder biomes, and not grow at all in freezing biomes")
    @ConfigEntry.Gui.Tooltip
    //TODO saplings, chorus, cactus =(
    //TODO this should probably be tag based, so crops only grow slower in biomes they don't like
    public boolean cropsGrowSlowerInCold = false;

    @ConfigEntry.Category("blocks")
    @Comment("New Minecarts")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject
    public CampfireTweaks campfireTweaks = new CampfireTweaks();

    public static class CampfireTweaks {
        @ConfigEntry.Category("blocks")
        @Comment("With this rule on, campfires will give nearby players effects")
        @ConfigEntry.Gui.Tooltip
        public boolean campfireEffects = true;

        @ConfigEntry.Category("blocks")
        @Comment("Campfire effects range")
        @ConfigEntry.Gui.Tooltip
        public int campfireEffectsRange = 10;

        @ConfigEntry.Category("blocks")
        @Comment("List of effect to give the player. put the Identifier of your effects here")
        @ConfigEntry.Gui.Tooltip(count = 2)
        public List<String> campfireEffectsList = Arrays.asList("minecraft:regeneration");

        @ConfigEntry.Category("blocks")
        @Comment("List of effect amplifiers...")
        @ConfigEntry.Gui.Tooltip
        public List<Integer> campfireEffectsAmplifierList = Arrays.asList(0);
    }

    @ConfigEntry.Category("entities")
    @Comment("Enables a few improvement to make FM suck a little less")
    @ConfigEntry.Gui.Tooltip(count = 3)
    public boolean betterFurnaceMinecart = true;
    @ConfigEntry.Category("entities")
    @Comment("Set the max allowed fuel for the furnace minecart. default 45000, doesn't work if better furnace minecart is off")
    @ConfigEntry.Gui.Tooltip(count = 2)
    public int maxFurnaceMinecartFuel = 45000;

    @ConfigEntry.Category("entities")
    @Comment("New Minecarts")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject
    public NewMinecarts newMinecarts = new NewMinecarts();

    public static class NewMinecarts {
        @ConfigEntry.Category("entities")
        @Comment("Enables the best minecart, the anvil minecart")
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.RequiresRestart
        public boolean isAnvilMinecartOn = true;
        @ConfigEntry.Category("entities")
        @Comment("Enables the second best minecart, the note block minecart")
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.RequiresRestart
        public boolean isNoteBlockMinecartOn = true;
        @ConfigEntry.Category("entities")
        @Comment("Enables jukebox minecart, might be buggy, especially on servers")
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.RequiresRestart
        public boolean isJukeboxMinecartOn = true;
    }
    @ConfigEntry.Category("items")
    @Comment("Allows players to \"pick\" blocks via minecarts")
    @ConfigEntry.Gui.Tooltip
    //TODO picking up fused TNT
    public boolean minecartBlockPicking = true;
    @ConfigEntry.Category("items")
    @Comment("Allows players to \"pick\" spawners via minecarts, does nothing if Minecart Block Picking is off")
    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean minecartSpawnerPicking = true;
    @ConfigEntry.Category("misc")
    @Comment("enable additional debug info, this will spam your log into oblivion")
    @ConfigEntry.Gui.Tooltip
    public boolean debugMessages = false;
}
