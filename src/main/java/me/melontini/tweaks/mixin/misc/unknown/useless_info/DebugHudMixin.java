package me.melontini.tweaks.mixin.misc.unknown.useless_info;

import it.unimi.dsi.fastutil.longs.LongSet;
import me.melontini.tweaks.client.TweaksClient;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(DebugHud.class)
public class DebugHudMixin {
    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;getServerWorldDebugString()Ljava/lang/String;", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT, method = "getLeftText")
    private void mTweaks$leftText(CallbackInfoReturnable<List<String>> cir, String string, BlockPos blockPos, Entity entity, Direction direction, String string2, ChunkPos chunkPos, World world, LongSet longSet, List<String> list) {
        if (TweaksClient.TEXT != null) list.add(TweaksClient.TEXT);
    }
}
