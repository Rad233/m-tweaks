package me.melontini.tweaks.client.sound;

import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public class PersistentMovingSoundInstance extends MovingSoundInstance {
    private final ClientWorld world;
    private final int entityId;

    public PersistentMovingSoundInstance(SoundEvent soundEvent, SoundCategory soundCategory, int entityId, ClientWorld world) {
        super(soundEvent, soundCategory);
        this.volume = 3;
        this.pitch = 1;
        this.world = world;
        this.entityId = entityId;
    }

    @Override
    public void tick() {
        Entity entity = world.getEntityById(entityId);
        if (entity != null) {
            volume = 3;
            this.x = entity.getX();
            this.y = entity.getY();
            this.z = entity.getZ();
        } else {
            //this sucks
            volume = 0;
        }
    }
}
