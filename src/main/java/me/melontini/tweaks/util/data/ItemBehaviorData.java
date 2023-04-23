package me.melontini.tweaks.util.data;

public class ItemBehaviorData {
    public String item_id;
    public String[] item_commands;
    public String[] user_commands;
    public String[] server_commands;
    public boolean complement;
    public boolean spawn_colored_particles;
    public ParticleColors particle_colors;

    public static class ParticleColors {
        public int red;
        public int green;
        public int blue;
    }
}
