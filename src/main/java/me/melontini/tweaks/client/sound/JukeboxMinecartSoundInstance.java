package me.melontini.tweaks.client.sound;

import me.melontini.tweaks.entity.vehicle.JukeboxMinecartEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

import java.util.UUID;

public class JukeboxMinecartSoundInstance extends MovingSoundInstance {
    private final ClientWorld world;
    private final UUID entityId;
    private final ClientPlayerEntity player;

    public JukeboxMinecartSoundInstance(SoundEvent soundEvent, SoundCategory soundCategory, UUID entityId, ClientWorld world, ClientPlayerEntity player) {
        super(soundEvent, soundCategory);
        this.volume = 3;
        this.pitch = 1;
        this.world = world;
        this.entityId = entityId;
        this.player = player;
    }

    @Override
    public void tick() {
        //TODO maybe figure out why JBMC unloads so early on servers
        JukeboxMinecartEntity entity = (JukeboxMinecartEntity) world.getEntityLookup().get(entityId);
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
