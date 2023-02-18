package me.melontini.tweaks.mixin.entities.quick_cart_link;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.duck.LinkableMinecartsDuck;
import me.melontini.tweaks.util.ItemStackUtil;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@MixinRelatedConfigOption("simpleMinecartLinking")
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
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
                                mTweaks$failLink(player, Text.literal("de-sync"));
                                cir.setReturnValue(ActionResult.FAIL);
                            } else if (unlinking == minecart) {
                                mTweaks$failLink(player, Text.literal("thing-a-mabob"));
                                cir.setReturnValue(ActionResult.FAIL);
                            } else {
                                LinkableMinecartsDuck duck1 = (LinkableMinecartsDuck) unlinking;
                                if (duck1.mTweaks$getFollower() == minecart) {

                                    duck.mTweaks$setFollowing(null);
                                    duck1.mTweaks$setFollower(null);

                                    ItemStackUtil.spawn(minecart.getPos(), Items.CHAIN.getDefaultStack(), world);
                                    mTweaks$linkSuccess(player, Text.literal("unlink success"));
                                    cir.setReturnValue(ActionResult.SUCCESS);
                                } else {
                                    mTweaks$failLink(player, Text.literal("how"));
                                    cir.setReturnValue(ActionResult.FAIL);
                                }
                            }
                            Tweaks.UNLINKING_CARTS.remove(player);
                        } else if (Tweaks.LINKING_CARTS.containsKey(player)) {
                            var linkingTo = Tweaks.LINKING_CARTS.get(player);

                            if (linkingTo == null) {
                                mTweaks$failLink(player, Text.literal("de-sync"));
                                cir.setReturnValue(ActionResult.FAIL);
                            } else if (linkingTo == minecart) {
                                mTweaks$failLink(player, Text.literal("link to self"));
                                cir.setReturnValue(ActionResult.FAIL);
                            } else if (Math.abs(minecart.distanceTo(linkingTo) - 1) > 6) {
                                mTweaks$failLink(player, Text.literal("too far"));
                                cir.setReturnValue(ActionResult.FAIL);
                            } else {
                                duck.mTweaks$setFollowing(linkingTo);
                                LinkableMinecartsDuck duck1 = (LinkableMinecartsDuck) linkingTo;
                                duck1.mTweaks$setFollower(minecart);
                                mTweaks$linkSuccess(player, Text.literal("link success"));
                                cir.setReturnValue(ActionResult.SUCCESS);
                            }
                            Tweaks.LINKING_CARTS.remove(player);
                        } else if (duck.mTweaks$getFollower() != null) {
                            Tweaks.UNLINKING_CARTS.put(player, minecart);
                            mTweaks$linkSuccess(player, Text.literal("started unlink"));
                            cir.setReturnValue(ActionResult.SUCCESS);
                        } else {
                            Tweaks.LINKING_CARTS.put(player, minecart);
                            mTweaks$linkSuccess(player, Text.literal("started link"));
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
