package me.melontini.tweaks.mixin.entities.quick_cart_link;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.duck.LinkableMinecartsDuck;
import me.melontini.tweaks.util.MiscUtil;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@MixinRelatedConfigOption("simpleMinecartLinking")
@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public World world;

    @Shadow public abstract Box getBoundingBox();

    @Inject(at = @At("HEAD"), method = "remove")
    void removeLink(CallbackInfo callbackInformation) {
        if (Tweaks.CONFIG.simpleMinecartLinking) {
            if ((Object) this instanceof AbstractMinecartEntity) {
                LinkableMinecartsDuck accessor = (LinkableMinecartsDuck) this;
                LinkableMinecartsDuck follower = (LinkableMinecartsDuck) accessor.mTweaks$getFollower();
                LinkableMinecartsDuck following = (LinkableMinecartsDuck) accessor.mTweaks$getFollowing();

                if (follower != null) {
                    follower.mTweaks$setFollowing(null);
                }

                if (following != null) {
                    following.mTweaks$setFollower(null);
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", cancellable = true)
    void onRecalculateVelocity(Vec3d movement, CallbackInfoReturnable<Vec3d> cir) {
        if (Tweaks.CONFIG.simpleMinecartLinking) {
            List<Entity> collisions = this.world.getOtherEntities((Entity) (Object) this, getBoundingBox().stretch(movement));

            if ((Entity) (Object) this instanceof AbstractMinecartEntity) {
                for (Entity entity : collisions) {
                    if (!MiscUtil.shouldCollide((Entity) (Object) this, entity) && world.getBlockState(((AbstractMinecartEntity) (Object) this).getBlockPos()).getBlock() instanceof AbstractRailBlock) {
                        cir.setReturnValue(movement);
                        cir.cancel();
                    }
                }
            }
        }
    }
}
