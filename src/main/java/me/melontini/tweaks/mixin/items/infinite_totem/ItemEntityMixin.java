package me.melontini.tweaks.mixin.items.infinite_totem;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.registries.ItemRegistry;
import me.melontini.tweaks.util.BeaconUtil;
import me.melontini.tweaks.util.PlayerUtil;
import me.melontini.tweaks.util.WorldUtil;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.util.math.Box;
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
import java.util.Random;
import java.util.Set;

import static me.melontini.tweaks.Tweaks.MODID;

@MixinRelatedConfigOption({"totemSettings.enableInfiniteTotem", "totemSettings.enableTotemAscension"})
@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    private static final Set<ItemEntity> MTWEAKS$ITEMS = new HashSet<>();
    @Shadow
    @Final
    private static TrackedData<ItemStack> STACK;
    private final Random mTweaks$random = new Random();
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

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tick()V", shift = At.Shift.AFTER), method = "tick")
    private void mTweaks$tick(CallbackInfo ci) {
        if (Tweaks.CONFIG.totemSettings.enableTotemAscension && Tweaks.CONFIG.totemSettings.enableInfiniteTotem) {
            if (this.dataTracker.get(STACK).isOf(Items.TOTEM_OF_UNDYING)) {
                ItemEntity self = (ItemEntity) (Object) this;
                if (age % 35 == 0 && mTweaks$ascensionTicks == 0) mTweaks$beaconCheck();

                if (mTweaks$beacon.getLeft() != null && mTweaks$beacon.getRight() >= 4) {
                    if (!world.isClient) {
                        if (mTweaks$itemEntity == null) {
                            if (mTweaks$ascensionTicks > 0) --mTweaks$ascensionTicks;
                            if (age % 40 == 0) {
                                List<ItemEntity> list = world.getEntitiesByClass(ItemEntity.class, new Box(getPos().x + 0.5, getPos().y + 0.5, getPos().z + 0.5, getPos().x - 0.5, getPos().y - 0.5, getPos().z - 0.5),
                                        itemEntity1 -> itemEntity1.getDataTracker().get(STACK).isOf(Items.NETHER_STAR) && !MTWEAKS$ITEMS.contains(itemEntity1));

                                if (!list.isEmpty()) {
                                    mTweaks$itemEntity = list.stream().findAny().get();
                                    MTWEAKS$ITEMS.add(mTweaks$itemEntity);

                                    mTweaks$itemEntity.setPickupDelayInfinite();
                                    this.setPickupDelayInfinite();

                                    int i = mTweaks$itemEntity.getDataTracker().get(STACK).getCount() - 1;

                                    if (i != 0) {
                                        ItemStack entityStack = mTweaks$itemEntity.getDataTracker().get(STACK).copy();
                                        entityStack.setCount(i);
                                        mTweaks$itemEntity.getDataTracker().get(STACK).setCount(1);
                                        ItemEntity itemEntity1 = EntityType.ITEM.create(world);
                                        itemEntity1.setStack(entityStack);
                                        itemEntity1.setPos(mTweaks$itemEntity.getX(), mTweaks$itemEntity.getY() + 0.2, mTweaks$itemEntity.getZ());
                                        world.spawnEntity(itemEntity1);

                                        PacketByteBuf packetByteBuf = PacketByteBufs.create();
                                        packetByteBuf.writeUuid(mTweaks$itemEntity.getUuid());
                                        packetByteBuf.writeItemStack(mTweaks$itemEntity.getDataTracker().get(STACK));

                                        PacketByteBuf packetByteBuf2 = PacketByteBufs.create();
                                        packetByteBuf2.writeUuid(itemEntity1.getUuid());
                                        packetByteBuf2.writeItemStack(itemEntity1.getDataTracker().get(STACK));

                                        for (PlayerEntity player : PlayerUtil.findPlayersInRange(world, getBlockPos(), 85)) {
                                            ServerPlayNetworking.send((ServerPlayerEntity) player, new Identifier(MODID, "notify_client_about_stuff_please"), packetByteBuf);
                                            ServerPlayNetworking.send((ServerPlayerEntity) player, new Identifier(MODID, "notify_client_about_stuff_please"), packetByteBuf2);
                                        }
                                    }
                                }
                            }
                        } else {
                            if (!mTweaks$itemEntity.cannotPickup()) {
                                setToDefaultPickupDelay();
                                mTweaks$itemEntity = null;
                            } else {
                                if (mTweaks$ascensionTicks < 180 && mTweaks$beaconCheck()) {
                                    ++mTweaks$ascensionTicks;

                                    WorldUtil.crudeSetVelocity(mTweaks$itemEntity, mTweaks$itemEntity.getVelocity().x * .5, .07, mTweaks$itemEntity.getVelocity().z * .5);
                                    WorldUtil.crudeSetVelocity(self, getVelocity().x * .5, .07, getVelocity().z * .5);

                                    if (mTweaks$random.nextInt(13) == 0)
                                        WorldUtil.addParticle(world, ParticleTypes.END_ROD, self.getX(), self.getY(), self.getZ(), getVelocity().x, -.07, getVelocity().z);
                                    if (mTweaks$random.nextInt(13) == 0)
                                        WorldUtil.addParticle(world, ParticleTypes.END_ROD, mTweaks$itemEntity.getX(), mTweaks$itemEntity.getY(), mTweaks$itemEntity.getZ(), mTweaks$itemEntity.getVelocity().x, -.07, mTweaks$itemEntity.getVelocity().z);
                                } else if (mTweaks$ascensionTicks == 180) {
                                    mTweaks$ascensionTicks = 0;
                                    ItemStack stack = new ItemStack(ItemRegistry.INFINITE_TOTEM);
                                    ItemEntity itemEntity2 = new ItemEntity(world, self.getX(), self.getY(), self.getZ(), stack);
                                    world.spawnEntity(itemEntity2);

                                    if (!world.isClient)
                                        ((ServerWorld) world).spawnParticles(Tweaks.KNOCKOFF_TOTEM_PARTICLE, itemEntity2.getX(), itemEntity2.getY(), itemEntity2.getZ(), 19, mTweaks$random.nextDouble(0.4) - 0.2, mTweaks$random.nextDouble(0.4) - 0.2, mTweaks$random.nextDouble(0.4) - 0.2, 0.5);

                                    mTweaks$itemEntity.discard();
                                    self.discard();
                                } else if (!mTweaks$beaconCheck()) {
                                    setToDefaultPickupDelay();
                                    mTweaks$itemEntity.setToDefaultPickupDelay();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean mTweaks$beaconCheck() {
        BlockEntity entity = world.getBlockEntity(new BlockPos(getX(), world.getTopY(Heightmap.Type.WORLD_SURFACE, getBlockPos().getX(), getBlockPos().getZ()) - 1, getZ()));
        if (entity instanceof BeaconBlockEntity beaconBlock) {
            this.mTweaks$beacon = new Pair<>(beaconBlock, BeaconUtil.getLevelFromBlocks(world, beaconBlock.getPos(), List.of(Blocks.DIAMOND_BLOCK, Blocks.NETHERITE_BLOCK)));
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
