package me.melontini.tweaks.mixin.misc.unknown.wakeup;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.WorldUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Shadow public abstract void playSound(SoundEvent event, SoundCategory category, float volume, float pitch);

    @Inject(at = @At("HEAD"), method = "wakeUp(ZZ)V")
    private void mTweaks$wakeUp(boolean skipSleepTimer, boolean updateSleepingPlayers, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (Tweaks.CONFIG.unknown) {
            if (!player.world.isClient) if (new Random().nextInt(100000) == 0) {
                var pos = WorldUtil.pickRandomSpot(player.world, player.getBlockPos(), 10, new Random());
                if (pos != null) {
                    var stand = new ArmorStandEntity(player.world, pos.getX(), pos.getY(), pos.getZ());
                    var stack = new ItemStack(Items.PLAYER_HEAD);
                    var nbt = new NbtCompound();
                    nbt.putString("SkullOwner", player.getDisplayName().getString());
                    stack.setNbt(nbt);
                    stand.equipStack(EquipmentSlot.HEAD, stack);
                    player.world.spawnEntity(stand);
                    playSound(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.AMBIENT, 4, 1);
                }
            }
        }
    }
}
