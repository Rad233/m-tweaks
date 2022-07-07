package me.melontini.tweaks.mixin.baiting_villagers;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.entity.ai.goal.VillagerTemptGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.village.VillagerType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity {

    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/VillagerEntity;setVillagerData(Lnet/minecraft/village/VillagerData;)V", shift = At.Shift.AFTER), method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;Lnet/minecraft/village/VillagerType;)V")
    private void test(EntityType<? extends VillagerEntity> entityType, World world, VillagerType type, CallbackInfo ci) {
        //0 invasive changes detected
        if (Tweaks.CONFIG.villagersFollowEmeraldBlocks) this.goalSelector.add(6, new VillagerTemptGoal((VillagerEntity) (Object) this, 0.5, Ingredient.ofItems(Items.EMERALD_BLOCK), false));
    }
}
