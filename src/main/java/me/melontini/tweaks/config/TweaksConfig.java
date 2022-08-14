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
    @Comment("Pretty self-descriptive. Makes Bee Nests fall after some time, you can also shoot them with arrows")
    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean canBeeNestsFall = true;

    @ConfigEntry.Category("world")
    @Comment("With this rule on, crops will have to planted in biomes they like, otherwise they won't grow. This rule is data-driven! checkout [link] for more info")
    @ConfigEntry.Gui.Tooltip
    public boolean temperatureBasedCropGrowthSpeed = false;

    @ConfigEntry.Category("world")
    @Comment("Makes fires spread a lot faster and wider")
    @ConfigEntry.Gui.Tooltip
    public boolean quickFire = false;

    @ConfigEntry.Category("blocks")
    @Comment("Various Incubator Settings :)")
    @ConfigEntry.Gui.Tooltip(count = 2)
    @ConfigEntry.Gui.CollapsibleObject
    public IncubatorSettings incubatorSettings = new IncubatorSettings();

    public static class IncubatorSettings {
        @ConfigEntry.Category("blocks")
        @Comment("Enables a handful machine which will hatch eggs for you! (data-driven)")
        @ConfigEntry.Gui.Tooltip(count = 2)
        @ConfigEntry.Gui.RequiresRestart
        public boolean enableIncubator = true;

        @ConfigEntry.Category("blocks")
        @Comment("Makes Incubator hatch times a bit more random")
        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean incubatorRandomness = false;

        @ConfigEntry.Category("blocks")
        @Comment("Enables m-tweaks certified incubator recipe. Don't forget to run /reload")
        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean incubatorRecipe = false; //Used in JSON
    }

    @ConfigEntry.Category("blocks")
    @Comment("Makes fletching table a little more useful by allowing you to tighten bow string!")
    @ConfigEntry.Gui.Tooltip(count = 2)
    @ConfigEntry.Gui.RequiresRestart
    public boolean usefulFletching = true;

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
    @Comment("When a bee pollinates a flower, the flower has a chance to spread, similar to grass. This won't happen every pollination, since there's a 3-6 minute cooldown.")
    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean beeFlowerDuplication = true;

    @ConfigEntry.Category("entities")
    @Comment("Enables bee flower duplication for tall flowers, also disables bonemealing tall flowers.")
    @ConfigEntry.Gui.Tooltip
    public boolean beeTallFlowerDuplication = true;

    @ConfigEntry.Category("entities")
    @Comment("Makes villagers follow if you have an emerald block in hand")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart
    public boolean villagersFollowEmeraldBlocks = false;

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
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.RequiresRestart
        public boolean isAnvilMinecartOn = true;
        @ConfigEntry.Category("entities")
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.RequiresRestart
        public boolean isNoteBlockMinecartOn = true;
        @ConfigEntry.Category("entities")
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.RequiresRestart
        public boolean isJukeboxMinecartOn = true;
    }

    @ConfigEntry.Category("entities")
    @Comment("New Boats")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject
    public NewBoats newBoats = new NewBoats();

    public static class NewBoats {
        @ConfigEntry.Category("entities")
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.RequiresRestart
        public boolean isFurnaceBoatOn = true;

        @ConfigEntry.Category("entities")
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.RequiresRestart
        public boolean isTNTBoatOn = true;

        @ConfigEntry.Category("entities")
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.RequiresRestart
        public boolean isJukeboxBoatOn = true;

        @ConfigEntry.Category("entities")
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.RequiresRestart
        public boolean isHopperBoatOn = true;
    }

    @ConfigEntry.Category("items")
    @Comment(" . ")
    @ConfigEntry.Gui.Tooltip(count = 4)
    public boolean balancedMending = true;

    @ConfigEntry.Category("items")
    @Comment("Every 2 days you can blow the \"sing\" horn to summon a wandering trader.")
    @ConfigEntry.Gui.Tooltip(count = 3)
    public boolean tradingGoatHorn = true;

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
    @Comment("makes the player explode after taking any damage")
    @ConfigEntry.Gui.Tooltip
    public boolean minorInconvenience = false;

    @ConfigEntry.Category("misc")
    @Comment("Doesn't load mixins if their related option is disabled, improving mod compatibility. The only downside is that you'd need to restart the game after enabling/disabling any option.")
    @ConfigEntry.Gui.Tooltip(count = 2)
    @ConfigEntry.Gui.RequiresRestart
    public boolean compatMode = false;
    @ConfigEntry.Category("misc")
    @Comment("enable additional debug info, this will spam your log into oblivion")
    @ConfigEntry.Gui.Tooltip
    public boolean debugMessages = false;

    @ConfigEntry.Category("misc")
    @Comment("???")
    @ConfigEntry.Gui.RequiresRestart
    public boolean unknown = false;
}
