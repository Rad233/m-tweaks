package me.melontini.tweaks.util.data;

public class ItemBehaviorData {
    public String item_id;
    public CommandHolder on_entity_hit;
    public CommandHolder on_block_hit;
    public CommandHolder on_miss;
    public CommandHolder on_any_hit;
    public boolean complement;
    public boolean spawn_colored_particles;
    public ParticleColors particle_colors;

    public static class ParticleColors {
        public int red;
        public int green;
        public int blue;
    }

    public static class CommandHolder {
        public String[] item_commands;
        public String[] user_commands;
        public String[] server_commands;
        public String[] hit_entity_commands;
        public String[] hit_block_commands;
    }

}
