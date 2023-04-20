package me.melontini.tweaks.mixin.entities.quick_cart_link;

import me.melontini.crackerutil.util.TextUtil;
import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.duck.LinkableMinecartsDuck;
import me.melontini.tweaks.util.ItemStackUtil;
import me.melontini.tweaks.util.TweaksTexts;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.melontini.tweaks.util.MiscUtil.blockPosAsString;

@Mixin(PlayerEntity.class)
@MixinRelatedConfigOption("simpleMinecartLinking")
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    private static void mTweaks$spawnChainParticles(AbstractMinecartEntity entity) {
        if (!entity.world.isClient()) {
            ((ServerWorld) entity.world).spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.CHAIN.getDefaultState()), entity.getX(), entity.getY() + 0.3, entity.getZ(), 15, 0.5, 0.5, 0.5, 0.5);
        }
    }

    @Inject(at = @At("HEAD"), method = "interact", cancellable = true)
    private void mTweaks$interact(Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (Tweaks.CONFIG.simpleMinecartLinking) {
            if (entity instanceof AbstractMinecartEntity minecart) {
                if (!world.isClient()) {
                    PlayerEntity player = (PlayerEntity) (Object) this;
                    LinkableMinecartsDuck duck = (LinkableMinecartsDuck) minecart;
                    ItemStack stack = player.getStackInHand(hand);

                    if (stack.isOf(Items.CHAIN)) {
                        if (Tweaks.UNLINKING_CARTS.containsKey(player)) {
                            var unlinking = Tweaks.UNLINKING_CARTS.get(player);
                            if (unlinking == null) {
                                mTweaks$failLink(player, TweaksTexts.MINECART_LINK_DE_SYNC);
                                cir.setReturnValue(ActionResult.FAIL);
                            } else if (unlinking == minecart) {
                                mTweaks$failLink(player, TweaksTexts.MINECART_LINK_SELF);
                                cir.setReturnValue(ActionResult.FAIL);
                            } else {
                                LinkableMinecartsDuck duck1 = (LinkableMinecartsDuck) unlinking;
                                if (duck1.mTweaks$getFollower() == minecart) {

                                    duck.mTweaks$setFollowing(null);
                                    duck1.mTweaks$setFollower(null);

                                    ItemStackUtil.spawn(minecart.getPos(), Items.CHAIN.getDefaultStack(), world);

                                    mTweaks$spawnChainParticles(unlinking);
                                    mTweaks$spawnChainParticles(minecart);

                                    mTweaks$linkSuccess(player, TextUtil.translatable("m-tweaks.simpleMinecartLinking.finished_unlink",
                                            minecart.hasCustomName() ? minecart.getCustomName() : blockPosAsString(minecart.getBlockPos()),
                                            unlinking.hasCustomName() ? unlinking.getCustomName() : blockPosAsString(unlinking.getBlockPos())));
                                    cir.setReturnValue(ActionResult.SUCCESS);
                                } else {
                                    mTweaks$failLink(player, TweaksTexts.MINECART_LINK_WHAT);
                                    cir.setReturnValue(ActionResult.FAIL);
                                }
                            }
                            Tweaks.UNLINKING_CARTS.remove(player);
                        } else if (Tweaks.LINKING_CARTS.containsKey(player)) {
                            var linkingTo = Tweaks.LINKING_CARTS.get(player);

                            if (linkingTo == null) {
                                mTweaks$failLink(player, TweaksTexts.MINECART_LINK_DE_SYNC);
                                cir.setReturnValue(ActionResult.FAIL);
                            } else if (linkingTo == minecart) {
                                mTweaks$failLink(player, TweaksTexts.MINECART_LINK_SELF);
                                cir.setReturnValue(ActionResult.FAIL);
                            } else if (Math.abs(minecart.distanceTo(linkingTo) - 1) > 6) {
                                mTweaks$failLink(player, TweaksTexts.MINECART_LINK_TOO_FAR);
                                cir.setReturnValue(ActionResult.FAIL);
                            } else {
                                LinkableMinecartsDuck duck1 = (LinkableMinecartsDuck) linkingTo;

                                duck.mTweaks$setFollowing(linkingTo);
                                duck1.mTweaks$setFollower(minecart);

                                if (!player.getAbilities().creativeMode) stack.decrement(1);

                                mTweaks$spawnChainParticles(linkingTo);
                                mTweaks$spawnChainParticles(minecart);

                                mTweaks$linkSuccess(player, TextUtil.translatable("m-tweaks.simpleMinecartLinking.finished_link",
                                        minecart.hasCustomName() ? minecart.getCustomName() : blockPosAsString(minecart.getBlockPos()),
                                        linkingTo.hasCustomName() ? linkingTo.getCustomName() : blockPosAsString(linkingTo.getBlockPos())));
                                cir.setReturnValue(ActionResult.SUCCESS);
                            }
                            Tweaks.LINKING_CARTS.remove(player);
                        } else if (duck.mTweaks$getFollower() != null) {
                            Tweaks.UNLINKING_CARTS.put(player, minecart);
                            ((ServerWorld) entity.world).spawnParticles(ParticleTypes.HAPPY_VILLAGER, minecart.getX(), minecart.getY() + 0.2, minecart.getZ(), 10, 0.5, 0.5, 0.5, 0.5);
                            mTweaks$linkSuccess(player, TextUtil.translatable("m-tweaks.simpleMinecartLinking.start_unlink",
                                    minecart.hasCustomName() ? minecart.getCustomName() : blockPosAsString(minecart.getBlockPos())));
                            cir.setReturnValue(ActionResult.SUCCESS);
                        } else {
                            Tweaks.LINKING_CARTS.put(player, minecart);
                            ((ServerWorld) entity.world).spawnParticles(ParticleTypes.HAPPY_VILLAGER, minecart.getX(), minecart.getY() + 0.2, minecart.getZ(), 10, 0.5, 0.5, 0.5, 0.5);
                            mTweaks$linkSuccess(player, TextUtil.translatable("m-tweaks.simpleMinecartLinking.start_link",
                                    minecart.hasCustomName() ? minecart.getCustomName() : blockPosAsString(minecart.getBlockPos())));
                            cir.setReturnValue(ActionResult.SUCCESS);
                        }
                    }
                }
            }
        }
    }

    private void mTweaks$failLink(PlayerEntity player, MutableText msg) {
        player.sendMessage(msg.formatted(Formatting.RED), true);
    }

    private void mTweaks$linkSuccess(PlayerEntity player, MutableText msg) {
        player.sendMessage(msg.formatted(Formatting.GREEN), true);
    }
}
