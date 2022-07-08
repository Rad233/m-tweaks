package me.melontini.tweaks.mixin.better_fletching_table;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.screens.FletchingScreenHandler;
import me.melontini.tweaks.util.LogUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.block.FletchingTableBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FletchingTableBlock.class)
public class FletchingTableBlockMixin extends CraftingTableBlock {
    public FletchingTableBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(at = @At("HEAD"), method = "onUse", cancellable = true)
    private void m_tweaks$onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (Tweaks.CONFIG.usefulFletching) if (state.isOf(Blocks.FLETCHING_TABLE)) {
            if (player.world.isClient)
                cir.setReturnValue(ActionResult.SUCCESS);

            player.openHandledScreen(this.createScreenHandlerFactory(state, world, pos));
            cir.setReturnValue(ActionResult.SUCCESS);
            LogUtil.info("HELLO");
        }
    }

    @Override /*how does mixin handle multiple @Overrides of the same class?*/
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedScreenHandlerFactory(((syncId, inv, player) -> new FletchingScreenHandler(syncId, inv, ScreenHandlerContext.create(world, pos))), Text.translatable("gui.m-tweaks.fletching"));
    }
}
