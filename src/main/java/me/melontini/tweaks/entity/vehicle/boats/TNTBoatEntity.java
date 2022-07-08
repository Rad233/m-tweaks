package me.melontini.tweaks.entity.vehicle.boats;

import me.melontini.tweaks.registries.EntityTypeRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;

import static me.melontini.tweaks.Tweaks.MODID;

public class TNTBoatEntity extends BoatEntityWithBlock {
    public int fuseTicks = -1;

    public TNTBoatEntity(EntityType<? extends BoatEntity> entityType, World world) {
        super(entityType, world);
    }

    public TNTBoatEntity(World world, double x, double y, double z) {
        this(EntityTypeRegistry.BOAT_WITH_TNT, world);
        this.setPosition(x, y, z);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
    }

    @Override
    public void tick() {
        if (this.fuseTicks > 0) {
            --this.fuseTicks;
            this.world.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
        } else if (this.fuseTicks == 0) {
            this.explode(this.getVelocity().horizontalLengthSquared());
        }

        if (this.horizontalCollision) {
            double d = this.getVelocity().horizontalLengthSquared();
            if ((this.getFirstPassenger() instanceof PlayerEntity)) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeUuid(this.getUuid());
                buf.writeDouble(d);
                ClientPlayNetworking.send(new Identifier(MODID, "boat_explosion_server"), buf);
            } else {
                this.explode(d);
            }
        }
        super.tick();
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        Entity entity = source.getSource();

        if (entity instanceof PersistentProjectileEntity persistentProjectileEntity && persistentProjectileEntity.isOnFire()) {
            this.explode(persistentProjectileEntity.getVelocity().lengthSquared());
            return false;
        }
        if (source.isFire()) {
            double d = this.getVelocity().horizontalLengthSquared();
            this.explode(d);
            return false;
        }

        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!this.world.isClient && !this.isRemoved()) {
            this.setDamageWobbleSide(-this.getDamageWobbleSide());
            this.setDamageWobbleTicks(10);
            this.setDamageWobbleStrength(this.getDamageWobbleStrength() + amount * 10.0F);
            this.scheduleVelocityUpdate();
            this.emitGameEvent(GameEvent.ENTITY_DAMAGE, source.getAttacker());
            boolean bl = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity) source.getAttacker()).getAbilities().creativeMode;
            if (bl || this.getDamageWobbleStrength() > 40.0F) {
                this.explode(0.09);
                return true;
            }

            return true;
        } else {
            return true;
        }
    }

    @Override
    public Item asItem() {
        return Registry.ITEM.get(Identifier.tryParse("m-tweaks:" + this.getBoatType().getName() + "_boat_with_tnt"));
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("MT-TNTFuse", 99)) {
            this.fuseTicks = nbt.getInt("MT-TNTFuse");
        }

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("MT_TNTFuse", this.fuseTicks);
    }

    public void explode(double velocity) {
        if (!this.world.isClient) {
            double d = Math.sqrt(velocity);
            if (d > 5.0) {
                d = 5.0;
            }

            this.discard();
            this.world.createExplosion(this, this.getX(), this.getY(), this.getZ(), (float) (4.0 + this.random.nextDouble() * 1.5 * d), Explosion.DestructionType.BREAK);
        }
    }
}
