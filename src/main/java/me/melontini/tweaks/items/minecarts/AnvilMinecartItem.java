package me.melontini.tweaks.items.minecarts;

import me.melontini.tweaks.entity.vehicle.minecarts.AnvilMinecartEntity;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.enums.RailShape;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

public class AnvilMinecartItem extends Item {
    //TODO minecart block picking
    private static final DispenserBehavior DISPENSER_BEHAVIOR = new ItemDispenserBehavior() {
        private final ItemDispenserBehavior defaultBehavior = new ItemDispenserBehavior();

        @Override
        public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
            World world = pointer.getWorld();
            double d = pointer.getX() + direction.getOffsetX() * 1.125;
            double e = MathHelper.fastFloor(pointer.getY()) + direction.getOffsetY();
            double f = pointer.getZ() + direction.getOffsetZ() * 1.125;
            BlockPos blockPos = pointer.getPos().offset(direction);
            BlockState blockState = world.getBlockState(blockPos);
            RailShape railShape = blockState.getBlock() instanceof AbstractRailBlock
                    ? blockState.get(((AbstractRailBlock) blockState.getBlock()).getShapeProperty())
                    : RailShape.NORTH_SOUTH;
            double g;
            if (blockState.isIn(BlockTags.RAILS)) {
                if (railShape.isAscending()) {
                    g = 0.6;
                } else {
                    g = 0.1;
                }
            } else {
                if (!blockState.isAir() || !world.getBlockState(blockPos.down()).isIn(BlockTags.RAILS)) {
                    return this.defaultBehavior.dispense(pointer, stack);
                }

                BlockState blockState2 = world.getBlockState(blockPos.down());
                RailShape railShape2 = blockState2.getBlock() instanceof AbstractRailBlock
                        ? blockState2.get(((AbstractRailBlock) blockState2.getBlock()).getShapeProperty())
                        : RailShape.NORTH_SOUTH;
                if (direction != Direction.DOWN && railShape2.isAscending()) {
                    g = -0.4;
                } else {
                    g = -0.9;
                }
            }

            AnvilMinecartEntity anvilMinecartEntity = new AnvilMinecartEntity(world, d, e + g, f);
            if (stack.hasCustomName()) {
                anvilMinecartEntity.setCustomName(stack.getName());
            }

            world.spawnEntity(anvilMinecartEntity);
            stack.decrement(1);
            return stack;
        }

        @Override
        protected void playSound(BlockPointer pointer) {
            pointer.getWorld().syncWorldEvent(WorldEvents.DISPENSER_DISPENSES, pointer.getPos(), 0);
        }
    };

    public AnvilMinecartItem(Settings settings) {
        super(settings);
        DispenserBlock.registerBehavior(this, DISPENSER_BEHAVIOR);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        if (!blockState.isIn(BlockTags.RAILS)) {
            return ActionResult.FAIL;
        } else {
            ItemStack itemStack = context.getStack();
            if (!world.isClient) {
                RailShape railShape = blockState.getBlock() instanceof AbstractRailBlock
                        ? blockState.get(((AbstractRailBlock) blockState.getBlock()).getShapeProperty())
                        : RailShape.NORTH_SOUTH;
                double d = 0.0;
                if (railShape.isAscending()) {
                    d = 0.5;
                }

                AnvilMinecartEntity anvilMinecartEntity = new AnvilMinecartEntity(world, (double) blockPos.getX() + 0.5, (double) blockPos.getY() + 0.0625 + d, (double) blockPos.getZ() + 0.5);
                if (itemStack.hasCustomName()) {
                    anvilMinecartEntity.setCustomName(itemStack.getName());
                }

                world.spawnEntity(anvilMinecartEntity);
                world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);
            }

            itemStack.decrement(1);
            return ActionResult.success(world.isClient);
        }
    }
}
