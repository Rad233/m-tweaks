package me.melontini.tweaks.util;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.duck.ThrowableBehaviorDuck;
import me.melontini.tweaks.util.data.ItemBehaviorData;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldEvents;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static me.melontini.tweaks.Tweaks.MODID;

public class ItemBehaviorAdder {

    public static final ItemBehavior DATA_PACK = (stack, flyingItemEntity, world, user, hitResult) -> {//default behavior to handle datapacks
        ItemBehaviorData data = Tweaks.ITEM_BEHAVIOR_DATA.get(stack.getItem());
        if (data == null) return;

        if (!world.isClient) {
            ServerWorld serverWorld = (ServerWorld) world;
            if (data.function_id != null) {
                Optional<CommandFunction> optional = serverWorld.getServer().getCommandFunctionManager().getFunction(Identifier.tryParse(data.function_id));
                if (optional.isPresent()) {
                    serverWorld.getServer().getCommandFunctionManager().execute(optional.get(), new ServerCommandSource(
                            serverWorld.getServer(), flyingItemEntity.getPos(), Vec2f.ZERO, serverWorld, 4, "MTFlyingItem", Text.literal("MTFlyingItem"), serverWorld.getServer(), flyingItemEntity));
                } else {
                    TweaksLog.error("Function {} was not found!", data.function_id);
                }
            }

            if (data.effect_id != null) {
                StatusEffect effect = PotionUtil.getStatusEffect(Identifier.tryParse(data.effect_id));
                StatusEffectInstance instance = new StatusEffectInstance(effect, data.effect_time, data.effect_level);

                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    Vec3d pos = hitResult.getPos();
                    List<LivingEntity> livingEntities = world.getEntitiesByClass(LivingEntity.class, new Box(((BlockHitResult) hitResult).getBlockPos()).expand(0.5), LivingEntity::isAlive);
                    livingEntities.stream().min(Comparator.comparingDouble(livingEntity -> livingEntity.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ())))
                            .ifPresent(livingEntity -> livingEntity.addStatusEffect(instance));
                } else if (hitResult.getType() == HitResult.Type.ENTITY) {
                    EntityHitResult entityHitResult = (EntityHitResult) hitResult;
                    Entity entity = entityHitResult.getEntity();
                    if (entity instanceof LivingEntity livingEntity) {
                        livingEntity.addStatusEffect(instance, user);
                    }
                }
            }

            if (data.particle_id != null) {
                Vec3d pos = hitResult.getPos();
                serverWorld.spawnParticles((DefaultParticleType) Registry.PARTICLE_TYPE.get(Identifier.tryParse(data.particle_id)), pos.getX(), pos.getY(), pos.getZ(), data.particle_count, data.particle_delta_x, data.particle_delta_y, data.particle_delta_z, data.particle_speed);
            }
        }
    };

    public static void init() {
        addBehavior(Items.BONE_MEAL, (stack, flyingItemEntity, world, user, hitResult) -> {
            if (!world.isClient && hitResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult result = (BlockHitResult) hitResult;
                BlockPos blockPos = result.getBlockPos();
                BlockPos blockPos2 = blockPos.offset(result.getSide());

                if (BoneMealItem.useOnFertilizable(stack, world, blockPos)) {
                    world.syncWorldEvent(WorldEvents.BONE_MEAL_USED, blockPos, 0);

                } else {
                    BlockState blockState = world.getBlockState(blockPos);
                    boolean bl = blockState.isSideSolidFullSquare(world, blockPos, result.getSide());
                    if (bl && BoneMealItem.useOnGround(stack, world, blockPos2, result.getSide())) {
                        world.syncWorldEvent(WorldEvents.BONE_MEAL_USED, blockPos2, 0);
                    }
                }
            }
        });
        addBehavior(Items.INK_SAC, (stack, flyingItemEntity, world, user, hitResult) -> {
            if (!world.isClient) {
                StatusEffectInstance instance = new StatusEffectInstance(StatusEffects.BLINDNESS, 100, 0);

                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    Vec3d pos = hitResult.getPos();
                    List<LivingEntity> livingEntities = world.getEntitiesByClass(LivingEntity.class, new Box(((BlockHitResult) hitResult).getBlockPos()).expand(0.5), LivingEntity::isAlive);
                    livingEntities.stream().min(Comparator.comparingDouble(livingEntity -> livingEntity.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ())))
                            .ifPresent(livingEntity -> livingEntity.addStatusEffect(instance));
                } else if (hitResult.getType() == HitResult.Type.ENTITY) {
                    EntityHitResult entityHitResult = (EntityHitResult) hitResult;
                    Entity entity = entityHitResult.getEntity();
                    if (entity instanceof LivingEntity livingEntity) {
                        livingEntity.addStatusEffect(instance, user);
                    }
                }

                Vec3d pos = hitResult.getPos();
                PacketByteBuf byteBuf = PacketByteBufs.create();
                byteBuf.writeDouble(pos.getX());
                byteBuf.writeDouble(pos.getY());
                byteBuf.writeDouble(pos.getZ());
                byteBuf.writeItemStack(stack);
                for (ServerPlayerEntity serverPlayerEntity : PlayerLookup.tracking(flyingItemEntity)) {
                    ServerPlayNetworking.send(serverPlayerEntity, new Identifier(MODID, "ink_sac_throw"), byteBuf);
                }
            }
            if (user instanceof PlayerEntity player) {
                player.getItemCooldownManager().set(stack.getItem(), 50);
            }
        });
    }

    public static void addBehavior(Item item, ItemBehavior behavior) {
        ((ThrowableBehaviorDuck) item).mTweaks$setBehavior(behavior);
    }
}
