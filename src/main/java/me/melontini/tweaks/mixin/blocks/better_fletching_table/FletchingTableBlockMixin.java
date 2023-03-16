package me.melontini.tweaks.mixin.blocks.better_fletching_table;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.screens.FletchingScreenHandler;
import me.melontini.tweaks.util.TweaksTexts;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.block.FletchingTableBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@MixinRelatedConfigOption("usefulFletching")
@Mixin(FletchingTableBlock.class)
public class FletchingTableBlockMixin extends CraftingTableBlock {
    public FletchingTableBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(at = @At("HEAD"), method = "onUse", cancellable = true)
    private void mTweaks$onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (Tweaks.CONFIG.usefulFletching) if (state.isOf(Blocks.FLETCHING_TABLE)) {
            if (player.world.isClient)
                cir.setReturnValue(ActionResult.SUCCESS);

            player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inv, player1) -> new FletchingScreenHandler(syncId, inv, ScreenHandlerContext.create(world, pos)), TweaksTexts.FLETCHING_SCREEN));
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
}
