package me.melontini.tweaks.networks;

import net.minecraft.util.Identifier;

import static me.melontini.tweaks.Tweaks.MODID;

public class TweaksPackets {
    public static final Identifier JUKEBOX_MINECART_START_PLAYING = new Identifier(MODID, "jukebox_minecart_start_playing");
    public static final Identifier JUKEBOX_MINECART_STOP_PLAYING = new Identifier(MODID, "jukebox_minecart_stop_playing");
    public static final Identifier USED_CUSTOM_TOTEM = new Identifier(MODID, "used_custom_totem");
    public static final Identifier FLYING_STACK_LANDED = new Identifier(MODID, "flying_stack_landed");
    public static final Identifier COLORED_FLYING_STACK_LANDED = new Identifier(MODID, "colored_flying_stack_landed");
    public static final Identifier ADD_ONE_PARTICLE = new Identifier(MODID, "add_one_particle");
    public static final Identifier EXPLODE_BOAT_ON_SERVER = new Identifier(MODID, "explode_boat_on_server");
}