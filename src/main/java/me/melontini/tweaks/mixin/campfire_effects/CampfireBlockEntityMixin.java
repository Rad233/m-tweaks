package me.melontini.tweaks.mixin.campfire_effects;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.LogUtil;
import me.melontini.tweaks.util.PlayerUtil;
import me.melontini.tweaks.util.PotionUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(CampfireBlockEntity.class)
public class CampfireBlockEntityMixin extends BlockEntity {
    public CampfireBlockEntityMixin(BlockEntityType<?> type) {
        super(type);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void mTweaks$litServerTick(CallbackInfo ci) {
        BlockState state = this.getCachedState();
        if (Tweaks.CONFIG.campfireTweaks.campfireEffects) {
            if (world.getTime() % 180 == 0) {
                if (state.get(CampfireBlock.LIT)) {
                    List<PlayerEntity> players = PlayerUtil.findPlayersInRange(world, pos, Tweaks.CONFIG.campfireTweaks.campfireEffectsRange);
                    for (PlayerEntity player : players) {
                        if (Tweaks.CONFIG.campfireTweaks.campfireEffectsList.size() == Tweaks.CONFIG.campfireTweaks.campfireEffectsAmplifierList.size()) {
                            for (int i = 0; i < Tweaks.CONFIG.campfireTweaks.campfireEffectsList.size(); i++) {
                                StatusEffectInstance effectInstance = new StatusEffectInstance(
                                        PotionUtil.getStatusEffect(Identifier.tryParse(Tweaks.CONFIG.campfireTweaks.campfireEffectsList.get(i))),
                                        200,
                                        Tweaks.CONFIG.campfireTweaks.campfireEffectsAmplifierList.get(i),
                                        true,
                                        false,
                                        true
                                );
                                player.addStatusEffect(effectInstance);
                            }
                        } else {
                            LogUtil.error("campfireEffectsList & campfireEffectsAmplifierList don't match in size!");
                        }
                    }
                }
            }
        }
    }
}
