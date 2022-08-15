package me.melontini.tweaks.blocks.entities;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.blocks.IncubatorBlock;
import me.melontini.tweaks.registries.BlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

//make sure to close your eyes before looking here.
public class IncubatorBlockEntity extends BlockEntity implements SidedInventory {

    public DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    public ItemStack egg = ItemStack.EMPTY;
    public int processingTime = -1;
    private final Random jRandom = new Random();

    public IncubatorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockRegistry.INCUBATOR_BLOCK_ENTITY, pos, state);
    }

    @SuppressWarnings("unused")
    public static void tick(World world, BlockPos pos, BlockState state, IncubatorBlockEntity incubatorBlockEntity) {
        incubatorBlockEntity.tick();
    }

    public void tick() {
        if (this.processingTime > 0) {
            assert world != null;
            if (world.getBlockState(pos.down()).getBlock() instanceof CampfireBlock ||
                    world.getBlockState(pos.down().down()).getBlock() instanceof CampfireBlock) {
                if (!world.isClient) this.processingTime--;
                if (world.random.nextInt(4) == 0 && world.isClient) {
                    double i = (jRandom.nextDouble(0.6) - 0.3);
                    double j = (jRandom.nextDouble(0.6) - 0.3);
                    world.addParticle(ParticleTypes.SMOKE, (pos.getX() + 0.5) + i, pos.getY() + 0.5, (pos.getZ() + 0.5) + j, 0F, 0.07F, 0F);
                }
            }
        }
        assert world != null;
        if (!world.isClient()) {
            var state = world.getBlockState(this.pos);
            if (this.egg.isEmpty()) {
                ItemStack stack = this.inventory.get(0);
                if (!stack.isEmpty()) {
                    ItemStack stack1 = stack.copy();
                    var data = Tweaks.EGG_DATA.get(Registry.ITEM.getId(stack1.getItem()));
                    if (data != null) {
                        stack.decrement(1);
                        stack1.setCount(1);
                        this.egg = stack1;
                        this.processingTime = Tweaks.CONFIG.incubatorSettings.incubatorRandomness ? (int) (data.time + (Math.random() * (data.time * 0.3) * 2) - data.time * 0.3) : data.time;
                        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
                        markDirty();
                    }
                }
            }

            if (this.processingTime == 0) {
                if (Tweaks.EGG_DATA.containsKey(Registry.ITEM.getId(this.egg.getItem()))) {
                    var data = Tweaks.EGG_DATA.get(Registry.ITEM.getId(this.egg.getItem()));
                    Entity entity = Registry.ENTITY_TYPE.get(Identifier.tryParse(data.entity)).create(world);
                    var entityPos = pos.offset(state.get(IncubatorBlock.FACING));
                    assert entity != null;
                    entity.setPos(entityPos.getX() + 0.5, entityPos.getY() + 0.5, entityPos.getZ() + 0.5);
                    if (entity instanceof PassiveEntity) {
                        ((PassiveEntity) entity).setBaby(true);
                    }
                    world.spawnEntity(entity);
                    this.egg = ItemStack.EMPTY;
                    this.processingTime = -1;
                    world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
                    markDirty();
                }
            }
        }
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        this.writeNbt(nbtCompound);
        return nbtCompound;
    }

    public boolean takeEgg(PlayerEntity player, ItemStack stack) {
        if (!stack.isEmpty()) {
            return false;
        } else {
            if (!this.inventory.get(0).isEmpty()) {
                player.getInventory().insertStack(this.inventory.get(0));
                this.inventory.set(0, ItemStack.EMPTY);
                markDirty();
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean insertEgg(ItemStack stack) {
        ItemStack stack1 = stack.copy();
        ItemStack slot = this.inventory.get(0);
        if (slot.isEmpty()) {
            this.inventory.set(0, stack1);
            stack.setCount(0);
            markDirty();
            return true;
        } else if (slot.getItem() == stack.getItem()) {
            int a = slot.getCount();
            int b = stack1.getCount();
            if (a + b <= slot.getMaxCount()) {
                this.inventory.set(0, stack1);
                this.inventory.get(0).setCount(a + b);
                stack.setCount(0);
                markDirty();
            } else if (a + b > slot.getMaxCount()) {
                int c = a + b;
                this.inventory.set(0, stack1);
                this.inventory.get(0).setCount(stack1.getMaxCount());
                stack.setCount(MathHelper.clamp(c - stack1.getMaxCount(), 0, stack1.getMaxCount()));
                markDirty();
            }
            return true;
        }
        return false;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.processingTime = nbt.getInt("ProcessingTime");

        if (nbt.contains("EggItem", 10)) {
            this.egg = (ItemStack.fromNbt(nbt.getCompound("EggItem")));
        }
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.inventory);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("ProcessingTime", this.processingTime);

        if (!this.egg.isEmpty())
            nbt.put("EggItem", this.egg.writeNbt(new NbtCompound()));
        Inventories.writeNbt(nbt, this.inventory);
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack itemStack = Inventories.splitStack(this.inventory, slot, amount);
        if (!itemStack.isEmpty()) {
            this.markDirty();
        }

        return itemStack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[]{0};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        assert world != null;
        return dir != world.getBlockState(this.pos).get(IncubatorBlock.FACING) && Tweaks.EGG_DATA.containsKey(Registry.ITEM.getId(stack.getItem()));
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        assert world != null;
        return dir != world.getBlockState(this.pos).get(IncubatorBlock.FACING);
    }
}