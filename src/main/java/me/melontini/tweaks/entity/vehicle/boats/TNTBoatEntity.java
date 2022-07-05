package me.melontini.tweaks.entity.vehicle.boats;

import me.melontini.tweaks.registries.EntityTypeRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
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
            if ((this.getFirstPassenger() instanceof PlayerEntity player)) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeUuid(this.getUuid());
                buf.writeDouble(d);
                ClientPlayNetworking.send(new Identifier(MODID, "boat_explosion_server"), buf);
            } else {
                this.explode(d);
                this.discard();
            }
        }
        super.tick();
    }

    @Override
    public float getEffectiveExplosionResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState, float max) {
        return !this.isPrimed() || !blockState.isIn(BlockTags.RAILS) && !world.getBlockState(pos.up()).isIn(BlockTags.RAILS)
                ? super.getEffectiveExplosionResistance(explosion, world, pos, blockState, fluidState, max)
                : 0.0F;
    }

    @Override
    public boolean canExplosionDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float explosionPower) {
        return (!this.isPrimed() || !state.isIn(BlockTags.RAILS) && !world.getBlockState(pos.up()).isIn(BlockTags.RAILS)) && super.canExplosionDestroyBlock(explosion, world, pos, state, explosionPower);
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

        return super.damage(source, amount);
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        if (fallDistance >= 3.0F) {
            float f = fallDistance / 10.0F;
            this.explode(f * f);
        }

        return super.handleFallDamage(fallDistance, damageMultiplier, damageSource);
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

    public boolean isPrimed() {
        return this.fuseTicks > -1;
    }

    public void explode(double velocity) {
        if (!this.world.isClient) {
            double d = Math.sqrt(velocity);
            if (d > 5.0) {
                d = 5.0;
            }

            this.world.createExplosion(this, this.getX(), this.getY(), this.getZ(), (float) (4.0 + this.random.nextDouble() * 1.5 * d), Explosion.DestructionType.BREAK);
            this.discard();
        }
    }

    public void prime() {
        this.fuseTicks = 80;
        if (!this.world.isClient) {
            this.world.sendEntityStatus(this, (byte) 10);
            if (!this.isSilent()) {
                this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }

    }
}
