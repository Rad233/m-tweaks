package me.melontini.tweaks.mixin.entities.quick_cart_link;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.duck.LinkableMinecartsDuck;
import me.melontini.tweaks.util.ItemStackUtil;
import me.melontini.tweaks.util.MiscUtil;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@MixinRelatedConfigOption("simpleMinecartLinking")
@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin extends Entity implements LinkableMinecartsDuck {
    @Unique
    private AbstractMinecartEntity mTweaks$following;
    @Unique
    private AbstractMinecartEntity mTweaks$follower;
    @Unique
    private UUID mTweaks$followingUUID;
    @Unique
    private UUID mTweaks$followerUUID;

    public AbstractMinecartEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    private static void mTweaks$spawnChainParticles(AbstractMinecartEntity entity) {
        if (!entity.world.isClient()) {
            ((ServerWorld) entity.world).spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.CHAIN.getDefaultState()), entity.getX(), entity.getY() + 0.3, entity.getZ(), 15, 0.5, 0.5, 0.5, 0.5);
        }
    }

    @Shadow
    protected abstract double getMaxSpeed();

    @Inject(at = @At("HEAD"), method = "tick")
    private void mTweaks$tick(CallbackInfo ci) {
        if (Tweaks.CONFIG.simpleMinecartLinking) {
            if (!world.isClient()) {
                if (mTweaks$getFollowing() != null) {
                    double dist = Math.abs(distanceTo(mTweaks$getFollowing()) - 1.2d);
                    Vec3d start = getPos().relativize(mTweaks$getFollowing().getPos()).normalize();
                    Vec3d vec3d = new Vec3d(Math.min(start.getX(), getMaxSpeed()), Math.min(start.getY(), getMaxSpeed()), Math.min(start.getZ(), getMaxSpeed()));

                    if (dist <= 0.7) {
                        if (dist <= 0.3) {
                            setVelocity(Vec3d.ZERO);
                        } else {
                            setVelocity(vec3d.multiply(dist * 0.75));
                        }
                    } else {
                        if (dist <= 6) {
                            setVelocity(vec3d);
                        } else {
                            LinkableMinecartsDuck duck = (LinkableMinecartsDuck) mTweaks$getFollowing();

                            mTweaks$spawnChainParticles(mTweaks$getFollowing());
                            mTweaks$spawnChainParticles((AbstractMinecartEntity) (Object) this);

                            duck.mTweaks$setFollower(null);
                            mTweaks$setFollowing(null);

                            ItemStackUtil.spawn(getPos(), Items.CHAIN.getDefaultStack(), world);
                        }
                    }
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "pushAwayFrom", cancellable = true)
    void onPushAway(Entity entity, CallbackInfo ci) {
        if (Tweaks.CONFIG.simpleMinecartLinking) {
            if (!MiscUtil.shouldCollide(this, entity)) {
                ci.cancel();
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "writeCustomDataToNbt")
    private void mTweaks$write(NbtCompound nbt, CallbackInfo ci) {
        if (mTweaks$followingUUID != null) {
            nbt.putUuid("MT-Following", mTweaks$followingUUID);
        }

        if (mTweaks$followerUUID != null) {
            nbt.putUuid("MT-Follower", mTweaks$followerUUID);
        }
    }

    @Inject(at = @At("RETURN"), method = "readCustomDataFromNbt")
    private void mTweaks$read(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("MT-Following")) {
            mTweaks$followingUUID = nbt.getUuid("MT-Following");
        }

        if (nbt.contains("MT-Follower")) {
            mTweaks$followerUUID = nbt.getUuid("MT-Follower");
        }
    }

    @Override
    public AbstractMinecartEntity mTweaks$getFollowing() {
        if (mTweaks$following == null) {
            mTweaks$following = (AbstractMinecartEntity) world.getEntityLookup().get(mTweaks$followingUUID);
        }
        return mTweaks$following;
    }

    public void mTweaks$setFollowing(AbstractMinecartEntity following) {
        this.mTweaks$following = following;
        this.mTweaks$followingUUID = following != null ? following.getUuid() : null;
    }

    @Override
    public AbstractMinecartEntity mTweaks$getFollower() {
        if (mTweaks$follower == null) {
            mTweaks$follower = (AbstractMinecartEntity) world.getEntityLookup().get(mTweaks$followerUUID);
        }
        return mTweaks$follower;
    }

    public void mTweaks$setFollower(AbstractMinecartEntity follower) {
        this.mTweaks$follower = follower;
        this.mTweaks$followerUUID = follower != null ? follower.getUuid() : null;
    }
}
