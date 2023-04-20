package me.melontini.tweaks.mixin.items.infinite_totem;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.registries.ItemRegistry;
import me.melontini.tweaks.util.BeaconUtil;
import me.melontini.tweaks.util.WorldUtil;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static me.melontini.tweaks.Tweaks.MODID;

@Mixin(ItemEntity.class)
@MixinRelatedConfigOption({"totemSettings.enableInfiniteTotem", "totemSettings.enableTotemAscension"})
public abstract class ItemEntityMixin extends Entity {
    private static final Set<ItemEntity> MTWEAKS$ITEMS = new HashSet<>();
    @Shadow
    @Final
    private static TrackedData<ItemStack> STACK;

    private final List<Block> beaconBlocks = List.of(Blocks.DIAMOND_BLOCK, Blocks.NETHERITE_BLOCK);
    private int mTweaks$ascensionTicks;
    private ItemEntity mTweaks$itemEntity;
    private Pair<BeaconBlockEntity, Integer> mTweaks$beacon = new Pair<>(null, 0);

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract void setPickupDelayInfinite();

    @Shadow
    public abstract void setToDefaultPickupDelay();

    @Shadow private int itemAge;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tick()V", shift = At.Shift.BEFORE), method = "tick")
    private void mTweaksTick(CallbackInfo ci) {
        if (!Tweaks.CONFIG.totemSettings.enableTotemAscension || !Tweaks.CONFIG.totemSettings.enableInfiniteTotem)
            return;
        if (!this.dataTracker.get(STACK).isOf(Items.TOTEM_OF_UNDYING)) return;

        ItemEntity self = (ItemEntity) (Object) this;
        if (age % 35 == 0 && mTweaks$ascensionTicks == 0) {
            if (!mTweaks$beaconCheck()) {
                this.setToDefaultPickupDelay();
                if (mTweaks$itemEntity != null) mTweaks$itemEntity.setToDefaultPickupDelay();
            }
        }

        if (mTweaks$beacon.getLeft() != null && mTweaks$beacon.getRight() >= 4) {
            if (!world.isClient) {
                if (mTweaks$itemEntity == null) {
                    if (mTweaks$ascensionTicks > 0) --mTweaks$ascensionTicks;

                    if (age % 10 == 0) {
                        Optional<ItemEntity> optional = world.getEntitiesByClass(ItemEntity.class, getBoundingBox().expand(0.5), itemEntity -> itemEntity.getDataTracker().get(STACK).isOf(Items.NETHER_STAR) && !MTWEAKS$ITEMS.contains(itemEntity)).stream().findAny();

                        if (optional.isPresent()) {
                            mTweaks$itemEntity = optional.get();

                            if (MTWEAKS$ITEMS.contains(mTweaks$itemEntity)) {
                                mTweaks$itemEntity = null;
                                return;
                            }

                            ItemStack targetStack = mTweaks$itemEntity.getDataTracker().get(STACK);
                            int count = targetStack.getCount() - 1;
                            if (count > 0) {
                                ItemStack newStack = targetStack.copy();
                                newStack.setCount(count);
                                targetStack.setCount(1);

                                mTweaks$itemEntity.getDataTracker().set(STACK, targetStack);

                                ItemEntity entity = new ItemEntity(world, mTweaks$itemEntity.getX(), mTweaks$itemEntity.getY(), mTweaks$itemEntity.getZ(), newStack);
                                world.spawnEntity(entity);

                                PacketByteBuf buf = PacketByteBufs.create();
                                buf.writeVarInt(mTweaks$itemEntity.getId());
                                buf.writeItemStack(targetStack);
                                for (ServerPlayerEntity serverPlayerEntity : PlayerLookup.tracking(this)) {
                                    ServerPlayNetworking.send(serverPlayerEntity, new Identifier(MODID, "notify_client_about_stuff_please"), buf);
                                }
                            }

                            mTweaks$itemEntity.setPickupDelayInfinite();
                            this.setPickupDelayInfinite();
                        }
                    }
                } else {
                    if (mTweaks$beaconCheck()) {
                        mTweaks$ascensionTicks++;

                        WorldUtil.crudeSetVelocity(this,0, 0.07, 0);
                        WorldUtil.crudeSetVelocity(mTweaks$itemEntity,0, 0.07, 0);

                        if (mTweaks$ascensionTicks == 180) {
                            mTweaks$ascensionTicks = 0;

                            ((ServerWorld) world).spawnParticles(ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), 15, 0, 0, 0, 0.4);

                            ItemEntity entity = new ItemEntity(world, this.getX(), this.getY(), this.getZ(), new ItemStack(ItemRegistry.INFINITE_TOTEM));
                            this.discard();
                            mTweaks$itemEntity.discard();
                            world.spawnEntity(entity);
                        }
                    } else {
                        this.setToDefaultPickupDelay();
                        mTweaks$itemEntity.setToDefaultPickupDelay();

                        mTweaks$itemEntity = null;
                    }
                }
            } else {

            }
        }
    }

    private boolean mTweaks$beaconCheck() {
        BlockEntity entity = world.getBlockEntity(new BlockPos(getX(), world.getTopY(Heightmap.Type.WORLD_SURFACE, getBlockPos().getX(), getBlockPos().getZ()) - 1, getZ()));
        if (entity instanceof BeaconBlockEntity beaconBlock) {
            this.mTweaks$beacon = new Pair<>(beaconBlock, BeaconUtil.getLevelFromBlocks(world, beaconBlock.getPos(), beaconBlocks));
            return true;
        } else {
            this.mTweaks$beacon = new Pair<>(null, 0);
            return false;
        }
    }

    @Inject(at = @At("TAIL"), method = "readCustomDataFromNbt")
    private void mTweaks$readNbt(NbtCompound nbt, CallbackInfo ci) {
        this.mTweaks$ascensionTicks = nbt.getInt("MT-Ascension");
    }

    @Inject(at = @At("TAIL"), method = "writeCustomDataToNbt")
    private void mTweaks$writeNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("MT-Ascension", this.mTweaks$ascensionTicks);
    }
}
