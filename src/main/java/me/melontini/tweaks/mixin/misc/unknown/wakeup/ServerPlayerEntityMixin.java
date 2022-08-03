package me.melontini.tweaks.mixin.misc.unknown.wakeup;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.LogUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Shadow public abstract void playSound(SoundEvent event, SoundCategory category, float volume, float pitch);

    @Inject(at = @At("HEAD"), method = "wakeUp(ZZ)V")
    private void mTweaks$wakeUp(boolean skipSleepTimer, boolean updateSleepingPlayers, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (Tweaks.CONFIG.unknown) {
            if (!player.world.isClient) if (Random.create().nextInt(1000000) == 0) {
                var pos = pickRandomSpot(player.world, player, 10, Random.create());
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

    @Unique
    private boolean isClear(World world, BlockPos pos) {
        List<Direction> dirMap = List.of(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST);
        for (Direction dir : dirMap) {
            if (!world.getBlockState(pos.offset(dir)).isAir()) {
                return false;
            }
        }
        return true;
    }

    @Unique
    private BlockPos pickRandomSpot(World world, PlayerEntity player, int range, Random random) {
        BlockPos playerPos = player.getBlockPos();
        int i = 0;
        while (true) {
            ++i;
            if (i > 1000) {
                return null;
            }
            var pos = new BlockPos(playerPos.getX() + random.nextBetween(-range, range), playerPos.getY() + random.nextBetween(-range, range), playerPos.getZ() + random.nextBetween(-range, range));
            LogUtil.info(pos);
            if (world.getBlockState(pos.up()).isAir() && world.getBlockState(pos).isAir() && isClear(world, pos) && isClear(world, pos.up())) {
                LogUtil.info("SS: {}", pos);
                return pos;
            }
        }
    }
}
