package me.melontini.tweaks.util;

import me.melontini.crackerutil.util.ColorUtil;
import me.melontini.crackerutil.util.Utilities;
import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.duck.ThrowableBehaviorDuck;
import me.melontini.tweaks.entity.FlyingItemEntity;
import me.melontini.tweaks.networks.TweaksPackets;
import me.melontini.tweaks.util.data.ItemBehaviorData;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemBehaviorAdder {
    private static final Map<Item, Integer> COLOR_MAP = Utilities.consume(new HashMap<>(), map -> {//TODO do something lol
        map.put(Items.RED_DYE, ColorUtil.toColor(121, 28, 39));
        map.put(Items.BLUE_DYE, ColorUtil.toColor(5, 36, 99));
        map.put(Items.LIGHT_BLUE_DYE, ColorUtil.toColor(30, 65, 115));
        map.put(Items.BLACK_DYE, ColorUtil.toColor(5, 5, 8));
    });

    public static final ItemBehavior DATA_PACK = (stack, flyingItemEntity, world, user, hitResult) -> {//default behavior to handle datapacks
        ItemBehaviorData data = Tweaks.ITEM_BEHAVIOR_DATA.get(stack.getItem());
        if (data == null) return;

        if (!world.isClient) {
            ServerWorld serverWorld = (ServerWorld) world;
            if (data.item_commands != null) {
                ServerCommandSource source = new ServerCommandSource(
                        serverWorld.getServer(), flyingItemEntity.getPos(), new Vec2f(flyingItemEntity.getPitch(), flyingItemEntity.getYaw()), serverWorld, 4, "MTFlyingItem", Text.literal("MTFlyingItem"), serverWorld.getServer(), flyingItemEntity);
                for (String command : data.item_commands) {
                    serverWorld.getServer().getCommandManager().executeWithPrefix(source, command);
                }
            }

            if (data.user_commands != null && user != null) {
                ServerCommandSource source = new ServerCommandSource(
                        serverWorld.getServer(), user.getPos(), new Vec2f(user.getPitch(), user.getYaw()), serverWorld, 4, user.getEntityName(), Text.literal(user.getEntityName()), serverWorld.getServer(), user);
                for (String command : data.user_commands) {
                    serverWorld.getServer().getCommandManager().executeWithPrefix(source, command);
                }
            }

            if (data.server_commands != null) {
                for (String command : data.server_commands) {
                    serverWorld.getServer().getCommandManager().executeWithPrefix(serverWorld.getServer().getCommandSource(), command);
                }
            }

            sendParticlePacket(flyingItemEntity, flyingItemEntity.getPos(), stack, data.spawn_colored_particles, ColorUtil.toColor(data.particle_colors.red, data.particle_colors.green, data.particle_colors.blue));
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
                addEffects(hitResult, world, user, new StatusEffectInstance(StatusEffects.BLINDNESS, 100, 0));
                sendParticlePacket(flyingItemEntity, flyingItemEntity.getPos(), stack, true, ColorUtil.toColor(24, 27, 50));
            }
        });
        addBehavior(Items.GLOW_INK_SAC, (stack, flyingItemEntity, world, user, hitResult) -> {
            if (!world.isClient) {
                addEffects(hitResult, world, user, new StatusEffectInstance(StatusEffects.BLINDNESS, 100, 0), new StatusEffectInstance(StatusEffects.GLOWING, 100, 0));
                sendParticlePacket(flyingItemEntity, flyingItemEntity.getPos(), stack, true, ColorUtil.toColor(25, 49, 49));
            }
        });

        for (Map.Entry<Item, Integer> entry : COLOR_MAP.entrySet()) {
            addBehavior(entry.getKey(), (stack, flyingItemEntity, world, user, hitResult) -> {
                if (!world.isClient) {
                    //addEffects(hitResult, world, user, new StatusEffectInstance(StatusEffects.BLINDNESS, 100, 0), new StatusEffectInstance(StatusEffects.GLOWING, 100, 0));
                    sendParticlePacket(flyingItemEntity, flyingItemEntity.getPos(), stack, true, entry.getValue());
                }
            });
        }
    }

    public static void addEffects(HitResult hitResult, World world, Entity user, StatusEffectInstance... instances) {
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            Vec3d pos = hitResult.getPos();
            List<LivingEntity> livingEntities = world.getEntitiesByClass(LivingEntity.class, new Box(((BlockHitResult) hitResult).getBlockPos()).expand(0.5), LivingEntity::isAlive);
            livingEntities.stream().min(Comparator.comparingDouble(livingEntity -> livingEntity.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ())))
                    .ifPresent(livingEntity -> {
                        for (StatusEffectInstance instance : instances) {
                            livingEntity.addStatusEffect(instance);
                        }
                    });
        } else if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult) hitResult;
            Entity entity = entityHitResult.getEntity();
            if (entity instanceof LivingEntity livingEntity) {
                for (StatusEffectInstance instance : instances) {
                    livingEntity.addStatusEffect(instance, user);
                }
            }
        }
    }

    public static void sendParticlePacket(FlyingItemEntity flyingItemEntity, Vec3d pos, ItemStack stack, boolean colored, int color) {
        PacketByteBuf byteBuf = PacketByteBufs.create();
        byteBuf.writeDouble(pos.getX());
        byteBuf.writeDouble(pos.getY());
        byteBuf.writeDouble(pos.getZ());
        byteBuf.writeItemStack(stack);
        byteBuf.writeBoolean(colored);
        byteBuf.writeVarInt(color);
        for (ServerPlayerEntity serverPlayerEntity : PlayerLookup.tracking(flyingItemEntity)) {
            ServerPlayNetworking.send(serverPlayerEntity, TweaksPackets.FLYING_STACK_LANDED, byteBuf);
        }
    }

    public static void addBehavior(Item item, ItemBehavior behavior) {
        ((ThrowableBehaviorDuck) item).mTweaks$setBehavior(behavior);
    }
}
