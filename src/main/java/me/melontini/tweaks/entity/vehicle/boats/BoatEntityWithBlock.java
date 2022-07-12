package me.melontini.tweaks.entity.vehicle.boats;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BoatEntityWithBlock extends BoatEntity {
    public BoatEntityWithBlock(EntityType<? extends BoatEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void updatePassengerPosition(Entity passenger) {
        if (this.hasPassenger(passenger)) {
            float f = 0.3F;
            float g = (float) ((!this.isAlive() ? 0.01F : this.getMountedHeightOffset()) + passenger.getHeightOffset());

            Vec3d vec3d = new Vec3d(f, 0.0, 0.0).rotateY(-this.getYaw(1) * (float) (Math.PI / 180.0) - (float) (Math.PI / 2));
            passenger.setPosition(this.getX() + vec3d.x, this.getY() + (double) g, this.getZ() + vec3d.z);
            passenger.setBodyYaw(passenger.getYaw(1) + this.yawVelocity);
            passenger.setHeadYaw(passenger.getHeadYaw() + this.yawVelocity);
            this.copyEntityData(passenger);
        }
    }

    @Override
    public boolean canAddPassenger(Entity passenger) {
        return this.getPassengerList().size() == 0 && !this.isSubmergedIn(FluidTags.WATER);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
