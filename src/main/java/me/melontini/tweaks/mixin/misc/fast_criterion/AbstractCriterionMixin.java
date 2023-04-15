package me.melontini.tweaks.mixin.misc.fast_criterion;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.Criterion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Mixin(value = AbstractCriterion.class, priority = 800)
@MixinRelatedConfigOption("concurrentInvChangedTrigger")
public class AbstractCriterionMixin<T  extends AbstractCriterionConditions> {
    @Mutable
    @Shadow
    @Final
    private Map<PlayerAdvancementTracker, Set<Criterion.ConditionsContainer<T>>> progressions;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void mTweaks$tweakMap(CallbackInfo ci) {
        if (Tweaks.CONFIG.concurrentInvChangedTrigger) this.progressions = Collections.synchronizedMap(this.progressions);
    }

    @Inject(method = "method_22512", at = @At(value = "HEAD"), cancellable = true)
    private static void mTweaks$tweakSet(PlayerAdvancementTracker manager, CallbackInfoReturnable<Set<?>> cir) {
        if (Tweaks.CONFIG.concurrentInvChangedTrigger) cir.setReturnValue(Collections.synchronizedSet(new ObjectOpenHashSet<>()));
    }
}
