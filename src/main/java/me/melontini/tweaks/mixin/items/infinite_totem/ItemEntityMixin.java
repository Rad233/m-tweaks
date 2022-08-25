package me.melontini.tweaks.mixin.items.infinite_totem;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.ducks.ItemEntityFriendAccess;
import me.melontini.tweaks.registries.ItemRegistry;
import me.melontini.tweaks.util.BeaconUtil;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@MixinRelatedConfigOption(value = {"totemSettings.enableInfiniteTotem", "totemSettings.enableTotemAscension"})
@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements ItemEntityFriendAccess {
    @Shadow
    @Final
    private static TrackedData<ItemStack> STACK;

    @Shadow public abstract void setPickupDelayInfinite();

    private int mTweaks$ascensionTicks;

    private Optional<ItemEntity> itemEntity = Optional.empty();


    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tick()V", shift = At.Shift.AFTER), method = "tick")
    private void mTweaks$tick(CallbackInfo ci) {
        if (Tweaks.CONFIG.totemSettings.enableTotemAscension && Tweaks.CONFIG.totemSettings.enableInfiniteTotem) {
            ItemEntity self = (ItemEntity) (Object) this;
            if (this.dataTracker.get(STACK).isOf(Items.TOTEM_OF_UNDYING)) {
                BlockEntity entity = world.getBlockEntity(new BlockPos(getX(), world.getTopY(Heightmap.Type.WORLD_SURFACE, getBlockPos().getX(), getBlockPos().getZ()) - 1, getZ()));
                if (entity instanceof BeaconBlockEntity blockEntity && BeaconUtil.getLevelFromBlocks(world, blockEntity.getPos(), List.of(Blocks.DIAMOND_BLOCK, Blocks.NETHERITE_BLOCK)) == 4) {
                    if (itemEntity.isEmpty()) {
                        if (mTweaks$ascensionTicks > 0) --mTweaks$ascensionTicks;
                        if (world.getTime() % 20 == 0) {
                            List<ItemEntity> list = world.getEntitiesByClass(ItemEntity.class, new Box(getPos().x + 0.5, getPos().y + 0.5, getPos().z + 0.5, getPos().x - 0.5, getPos().y - 0.5, getPos().z - 0.5), itemEntity1 -> itemEntity1.getDataTracker().get(STACK).isOf(Items.NETHER_STAR)).stream().filter(itemEntity -> ((ItemEntityFriendAccess) itemEntity).mTweaks$getFriend() == self || !((ItemEntityFriendAccess) itemEntity).mTweaks$hasFriend()).toList();
                            if (!list.isEmpty()) {
                                itemEntity = list.stream().min(Comparator.comparingDouble(itemEntity2 -> itemEntity2.squaredDistanceTo(self)));

                                int i = itemEntity.get().getDataTracker().get(STACK).getCount() - 1;
                                if (i != 0) {
                                    ItemStack entityStack = itemEntity.get().getDataTracker().get(STACK).copy();
                                    entityStack.setCount(i);
                                    itemEntity.get().getDataTracker().get(STACK).setCount(1);
                                    ItemEntity itemEntity1 = EntityType.ITEM.create(world);
                                    itemEntity1.setStack(entityStack);
                                    itemEntity1.setPos(itemEntity.get().getX(), itemEntity.get().getY() + 0.3, itemEntity.get().getZ());
                                    world.spawnEntity(itemEntity1);
                                }
                                itemEntity.get().setPickupDelayInfinite();
                                this.setPickupDelayInfinite();
                                ((ItemEntityFriendAccess) itemEntity.get()).mTweaks$setFriend(self);
                            }
                        }
                    } else {
                        if (((ItemEntityFriendAccess) itemEntity.get()).mTweaks$getFriend() != self) {
                            itemEntity = Optional.empty();
                        } else {
                            Random random1 = new Random();
                            if (mTweaks$ascensionTicks < 180) {
                                ++mTweaks$ascensionTicks;
                                itemEntity.get().setVelocity(itemEntity.get().getVelocity().x * .5, .07, itemEntity.get().getVelocity().z * .5);
                                self.setVelocity(getVelocity().x * .5, .07, getVelocity().z * .5);
                                if (world.isClient) {
                                    if (random1.nextInt(8) == 0)
                                        world.addParticle(ParticleTypes.END_ROD, self.getX(), self.getY(), self.getZ(), getVelocity().x, -.07, getVelocity().z);
                                    if (random1.nextInt(8) == 0)
                                        world.addParticle(ParticleTypes.END_ROD, itemEntity.get().getX(), itemEntity.get().getY(), itemEntity.get().getZ(), itemEntity.get().getVelocity().x, -.07, itemEntity.get().getVelocity().z);
                                }
                            } else if (mTweaks$ascensionTicks == 180) {
                                mTweaks$ascensionTicks = 0;
                                ItemStack stack = new ItemStack(ItemRegistry.INFINITE_TOTEM);
                                ItemEntity itemEntity2 = new ItemEntity(world, self.getX(), self.getY(), self.getZ(), stack);
                                world.spawnEntity(itemEntity2);
                                if (!world.isClient()) {
                                    ((ServerWorld) world).spawnParticles(Tweaks.KNOCKOFF_TOTEM_PARTICLE, itemEntity2.getX(), itemEntity2.getY(), itemEntity2.getZ(), 19, random1.nextDouble(0.4) - 0.2, random1.nextDouble(0.4) - 0.2, random1.nextDouble(0.4) - 0.2, 0.5);
                                }
                                itemEntity.get().discard();
                                self.discard();
                            }
                        }
                    }
                }
            }
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
