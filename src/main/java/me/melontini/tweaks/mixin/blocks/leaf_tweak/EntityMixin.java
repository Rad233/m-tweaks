package me.melontini.tweaks.mixin.blocks.leaf_tweak;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.melontini.tweaks.Tweaks.LEAF_SLOWNESS;

@MixinRelatedConfigOption("leafSlowdown")
@Mixin(LivingEntity.class)
public abstract class EntityMixin extends Entity {

    public EntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract @Nullable EntityAttributeInstance getAttributeInstance(EntityAttribute attribute);

    @Inject(at = @At("HEAD"), method = "baseTick")
    public void mTweaks$tick(CallbackInfo ci) {
        if (Tweaks.CONFIG.leafSlowdown) {
            EntityAttributeInstance attributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            if (!this.world.isClient) {
                if (this.world.getBlockState(getBlockPos().down()).isIn(BlockTags.LEAVES)
                        || (this.world.getBlockState(new BlockPos(getBlockPos().down(2))).isIn(BlockTags.LEAVES) && this.world.getBlockState(new BlockPos(getBlockPos().down())).isOf(Blocks.AIR))) {
                    if (((LivingEntity) (Object) this) instanceof PlayerEntity player && (player.isCreative() || player.isSpectator()))
                        return;
                    if (attributeInstance != null)
                        if (!attributeInstance.hasModifier(LEAF_SLOWNESS)) {
                            attributeInstance.addTemporaryModifier(LEAF_SLOWNESS);
                        }
                    /*Does this even work?*/
                    setVelocity(getVelocity().getX(), getVelocity().getY() * 0.7, getVelocity().getZ());
                } else {
                    if (attributeInstance != null)
                        if (attributeInstance.hasModifier(LEAF_SLOWNESS)) {
                            attributeInstance.removeModifier(LEAF_SLOWNESS);
                        }
                }
            }
        }
    }
}
