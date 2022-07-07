package me.melontini.tweaks.mixin.falling_beehives;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static me.melontini.tweaks.Tweaks.MODID;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockMixin extends Entity {
    @Shadow
    @Nullable
    public NbtCompound blockEntityData;
    @Shadow
    private BlockState block;

    public FallingBlockMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(at = @At(value = "INVOKE", target = "net/minecraft/world/World.getBlockEntity (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/entity/BlockEntity;", shift = At.Shift.AFTER), method = "tick")
    public void mTweaks$tick(CallbackInfo ci) {
        BlockPos blockPos = this.getBlockPos();
        BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
        if (blockEntity != null) {
            if (blockEntity instanceof BeehiveBlockEntity beehiveBlockEntity && Tweaks.CONFIG.canBeeNestsFall) {
                if (this.block.getBlock() == Blocks.BEE_NEST) {
                    NbtCompound nbt = blockEntityData;
                    assert nbt != null;
                    if (nbt.getBoolean("MT-FromFallenBlock")) {
                        nbt.putBoolean("MT-FromFallenBlock", false);
                        List<ItemStack> stacks = LootTableUtil.prepareLoot(world, new Identifier(MODID, "bee_nest/bee_nest_broken"));

                        List<PlayerEntity> players = PlayerUtil.findNonCreativePlayersInRange(world, this.getBlockPos(), 16);
                        NbtList nbeetlist = nbt.getList("Bees", 10);
                        List<BeeEntity> beeEntities = world.getNonSpectatingEntities(BeeEntity.class, new Box(getBlockPos()).expand(50));

                        if (!players.isEmpty()) {
                            world.breakBlock(beehiveBlockEntity.getPos(), false);
                            PlayerEntity player = MiscUtil.pickRandomEntryFromList(players);
                            for (int i = 0; i < nbeetlist.size(); ++i) {
                                BeeEntity bee = new BeeEntity(EntityType.BEE, world);
                                bee.setPosition(getPos());
                                bee.setTarget(player);
                                world.spawnEntity(bee);
                            }
                            for (BeeEntity bee : beeEntities) {
                                bee.setTarget(player);
                            }
                            for (ItemStack stack : stacks) {
                                ItemStackUtil.spawnItemWithRandVelocity(this.getPos(), stack, world, 0.5);
                            }
                        } else {
                            world.breakBlock(beehiveBlockEntity.getPos(), false);
                            for (int i = 0; i < nbeetlist.size(); ++i) {
                                BeeEntity bee = new BeeEntity(EntityType.BEE, world);
                                bee.setPosition(getPos());
                                bee.setCannotEnterHiveTicks(400);
                                world.spawnEntity(bee);
                            }
                            for (ItemStack stack : stacks) {
                                ItemStackUtil.spawnItemWithRandVelocity(this.getPos(), stack, world, 0.5);
                            }
                        }
                        LogUtil.info("broke Bee Nest generated from Falling Block");
                    }
                }
            }
        }
    }
}
