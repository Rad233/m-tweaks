package me.melontini.tweaks.mixin.misc.gui_particles;

import me.melontini.crackerutil.client.util.ScreenParticleHelper;
import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.mixin.misc.gui_particles.accessors.HandledScreenAccessor;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
@MixinRelatedConfigOption("guiParticles.anvilScreenParticles")
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory;setStack(ILnet/minecraft/item/ItemStack;)V", ordinal = 0), method = "onTakeOutput")
    private void mTweaks$particles(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (Tweaks.CONFIG.guiParticles.anvilScreenParticles) {
            try {
                if (MinecraftClient.getInstance().isOnThread() && MinecraftClient.getInstance().currentScreen instanceof AnvilScreen anvilScreen) {
                    BlockState state = Blocks.ANVIL.getDefaultState();
                    HandledScreenAccessor accessor = (HandledScreenAccessor) anvilScreen;
                    var slot = this.slots.get(2);
                    ScreenParticleHelper.addParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, state), accessor.mTweaks$getX() + slot.x + 8, accessor.mTweaks$getY() + slot.y + 8, 0.5, 0.5, 0.5, 5);
                }
            } catch (Exception e) {
                //client-server handling 101
            }
        }
    }
}
