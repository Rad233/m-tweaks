package me.melontini.tweaks.client.sound;

import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;

import java.util.UUID;

public class PersistentMovingSoundInstance extends MovingSoundInstance {
    private final ClientWorld world;
    private final UUID entityId;

    public PersistentMovingSoundInstance(SoundEvent soundEvent, SoundCategory soundCategory, UUID entityId, ClientWorld world, Random random) {
        super(soundEvent, soundCategory, random);
        this.volume = 3;
        this.pitch = 1;
        this.world = world;
        this.entityId = entityId;
    }

    @Override
    public void tick() {
        //TODO maybe figure out why JBMC unloads so early on servers
        Entity entity = world.getEntityLookup().get(entityId);
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
