package me.melontini.tweaks.mixin.misc.unknown.useless_info;

import it.unimi.dsi.fastutil.longs.LongSet;
import me.melontini.tweaks.client.TweaksClient;
import me.melontini.tweaks.util.LogUtil;
import me.melontini.tweaks.util.MiscUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(DebugHud.class)
public class DebugHudMixin {
    @Unique
    private String TEXT;

    @Inject(at = @At("TAIL"), method = "<init>")
    private void mTweaks$init(MinecraftClient client, CallbackInfo ci) {
        this.TEXT = MiscUtil.pickRandomEntryFromList(TweaksClient.USEFUL_TEXTS);
        LogUtil.info(TEXT);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;getServerWorldDebugString()Ljava/lang/String;", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT, method = "getLeftText")
    private void leftText(CallbackInfoReturnable<List<String>> cir, String string, BlockPos blockPos, Entity entity, Direction direction, String string2, ChunkPos chunkPos, World world, LongSet longSet, List<String> list) {
        list.add(TEXT);
    }
}
