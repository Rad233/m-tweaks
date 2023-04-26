package me.melontini.tweaks.util;

import me.melontini.crackerutil.util.ColorUtil;
import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.duck.ThrowableBehaviorDuck;
import me.melontini.tweaks.entity.FlyingItemEntity;
import me.melontini.tweaks.networks.TweaksPackets;
import me.melontini.tweaks.util.data.ItemBehaviorData;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.world.explosion.Explosion;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class ItemBehaviorAdder {
    private static final Set<Item> DYE_ITEMS = Set.of(
            Items.RED_DYE, Items.BLUE_DYE, Items.LIGHT_BLUE_DYE,
            Items.CYAN_DYE, Items.BLACK_DYE, Items.BROWN_DYE,
            Items.GREEN_DYE, Items.PINK_DYE,  Items.PURPLE_DYE,
            Items.YELLOW_DYE, Items.WHITE_DYE, Items.ORANGE_DYE,
            Items.LIME_DYE, Items.MAGENTA_DYE, Items.LIGHT_GRAY_DYE,
            Items.GRAY_DYE);

    public static final ItemBehavior DATA_PACK = (stack, flyingItemEntity, world, user, hitResult) -> {//default behavior to handle datapacks
        if (!world.isClient()) {
            ItemBehaviorData data = Tweaks.ITEM_BEHAVIOR_DATA.get(stack.getItem());
            if (data == null) return;

            ServerWorld serverWorld = (ServerWorld) world;

            switch (hitResult.getType()) {
                case ENTITY -> executeCommands(serverWorld, flyingItemEntity, user, hitResult, data.on_entity_hit);
                case BLOCK -> executeCommands(serverWorld, flyingItemEntity, user, hitResult, data.on_block_hit);
                case MISS -> executeCommands(serverWorld, flyingItemEntity, user, hitResult, data.on_miss);
            }
            executeCommands(serverWorld, flyingItemEntity, user, hitResult, data.on_any_hit);

            sendParticlePacket(flyingItemEntity, flyingItemEntity.getPos(), stack, data.spawn_colored_particles, ColorUtil.toColor(data.particle_colors.red, data.particle_colors.green, data.particle_colors.blue));
        }
    };

    private static void executeCommands(ServerWorld serverWorld, FlyingItemEntity flyingItemEntity, Entity user, HitResult hitResult, ItemBehaviorData.CommandHolder data) {
        if (data.item_commands != null) {
            ServerCommandSource source = new ServerCommandSource(
                    serverWorld.getServer(), flyingItemEntity.getPos(), new Vec2f(flyingItemEntity.getPitch(), flyingItemEntity.getYaw()), serverWorld, 4, "MTFlyingItem", Text.literal("MTFlyingItem"), serverWorld.getServer(), flyingItemEntity).withSilent();
            for (String command : data.item_commands) {
                serverWorld.getServer().getCommandManager().executeWithPrefix(source, command);
            }
        }

        if (data.user_commands != null && user != null) {
            ServerCommandSource source = new ServerCommandSource(
                    serverWorld.getServer(), user.getPos(), new Vec2f(user.getPitch(), user.getYaw()), serverWorld, 4, user.getEntityName(), Text.literal(user.getEntityName()), serverWorld.getServer(), user).withSilent();
            for (String command : data.user_commands) {
                serverWorld.getServer().getCommandManager().executeWithPrefix(source, command);
            }
        }

        if (data.server_commands != null) {
            for (String command : data.server_commands) {
                serverWorld.getServer().getCommandManager().executeWithPrefix(serverWorld.getServer().getCommandSource().withSilent(), command);
            }
        }

        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult) hitResult;
            Entity entity = entityHitResult.getEntity();
            if (entity instanceof LivingEntity) {
                if (data.hit_entity_commands != null) {
                    ServerCommandSource source = new ServerCommandSource(
                            serverWorld.getServer(), entity.getPos(), new Vec2f(entity.getPitch(), entity.getYaw()), serverWorld, 4, entity.getEntityName(), Text.literal(entity.getEntityName()), serverWorld.getServer(), entity).withSilent();
                    for (String command : data.hit_entity_commands) {
                        serverWorld.getServer().getCommandManager().executeWithPrefix(source, command);
                    }
                }
            }
        }
    }

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
            }
        });
        addBehavior(Items.GLOW_INK_SAC, (stack, flyingItemEntity, world, user, hitResult) -> {
            if (!world.isClient) {
                addEffects(hitResult, world, user, new StatusEffectInstance(StatusEffects.BLINDNESS, 100, 0), new StatusEffectInstance(StatusEffects.GLOWING, 100, 0));
            }
        });

        for (Item item : DYE_ITEMS) {
            addBehavior(item, (stack, flyingItemEntity, world, user, hitResult) -> {
                if (!world.isClient) {
                    if (hitResult.getType() == HitResult.Type.ENTITY) {
                        EntityHitResult entityHitResult = (EntityHitResult) hitResult;
                        Entity entity = entityHitResult.getEntity();
                        if (entity instanceof PlayerEntity player) {
                            PacketByteBuf buf = PacketByteBufs.create();
                            buf.writeItemStack(stack);

                            ServerPlayNetworking.send((ServerPlayerEntity) player, TweaksPackets.COLORED_FLYING_STACK_LANDED, buf);
                        } else {
                            Vec3d pos = hitResult.getPos();
                            List<PlayerEntity> playerEntities = world.getEntitiesByClass(PlayerEntity.class, new Box(new BlockPos(pos)).expand(0.5), LivingEntity::isAlive);
                            playerEntities.stream().min(Comparator.comparingDouble(player -> player.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ())))
                                    .ifPresent(player -> {
                                        PacketByteBuf buf = PacketByteBufs.create();
                                        buf.writeItemStack(stack);

                                        ServerPlayNetworking.send((ServerPlayerEntity) player, TweaksPackets.COLORED_FLYING_STACK_LANDED, buf);
                                    });
                        }
                    } else if (hitResult.getType() == HitResult.Type.BLOCK) {
                        Vec3d pos = hitResult.getPos();
                        List<PlayerEntity> playerEntities = world.getEntitiesByClass(PlayerEntity.class, new Box(((BlockHitResult) hitResult).getBlockPos()).expand(0.5), LivingEntity::isAlive);
                        playerEntities.stream().min(Comparator.comparingDouble(player -> player.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ())))
                                .ifPresent(player -> {
                                    PacketByteBuf buf = PacketByteBufs.create();
                                    buf.writeItemStack(stack);

                                    ServerPlayNetworking.send((ServerPlayerEntity) player, TweaksPackets.COLORED_FLYING_STACK_LANDED, buf);
                                });
                    }
                }
            });
        }

        addBehavior((stack, flyingItemEntity, world, user, hitResult) -> {
            if (!world.isClient) {
                if (hitResult.getType() == HitResult.Type.ENTITY) {
                    EntityHitResult entityHitResult = (EntityHitResult) hitResult;
                    Entity entity = entityHitResult.getEntity();
                    entity.damage(Tweaks.bricked(user), 2);
                    if (entity instanceof LivingEntity livingEntity) {
                        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 100, 0));
                    }
                    if (entity instanceof Angerable angerable && user instanceof LivingEntity livingEntity) {
                        angerable.setTarget(livingEntity);
                    }
                }
                world.spawnEntity(new ItemEntity(world, flyingItemEntity.getX(), flyingItemEntity.getY(), flyingItemEntity.getZ(), stack));
            }
        }, Items.BRICK, Items.NETHER_BRICK);

        addBehavior(Items.FIRE_CHARGE, (stack, flyingItemEntity, world, user, hitResult) -> {
            if (!world.isClient) {
                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    BlockHitResult result = (BlockHitResult) hitResult;
                    BlockPos blockPos = result.getBlockPos();
                    BlockState blockState = world.getBlockState(blockPos);

                    if (blockState.getBlock() instanceof TntBlock) {
                        TntBlock.primeTnt(world, blockPos);
                        world.removeBlock(blockPos, false);
                    } else if (FlammableBlockRegistry.getDefaultInstance().get(blockState.getBlock()) != null) {
                        world.setBlockState(blockPos.offset(result.getSide()), FireBlock.getState(world, blockPos.offset(result.getSide())));
                    }
                } else if (hitResult.getType() == HitResult.Type.ENTITY) {
                    EntityHitResult result = (EntityHitResult) hitResult;
                    Entity entity = result.getEntity();
                    entity.setOnFireFor(8);
                }
            }
        });

        addBehavior(Items.GUNPOWDER, (stack, flyingItemEntity, world, user, hitResult) -> {
            if (!world.isClient) {
                world.createExplosion(user, flyingItemEntity.getX(), flyingItemEntity.getY(), flyingItemEntity.getZ(), 1, Explosion.DestructionType.BREAK);
            }
        });
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
            } else {
                Vec3d pos = hitResult.getPos();
                List<LivingEntity> livingEntities = world.getEntitiesByClass(LivingEntity.class, new Box(new BlockPos(pos)).expand(0.5), LivingEntity::isAlive);
                livingEntities.stream().min(Comparator.comparingDouble(livingEntity -> livingEntity.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ())))
                        .ifPresent(livingEntity -> {
                            for (StatusEffectInstance instance : instances) {
                                livingEntity.addStatusEffect(instance);
                            }
                        });
            }
        }
    }

    public static void sendParticlePacketInt(FlyingItemEntity flyingItemEntity, Vec3d pos, ItemStack stack, boolean colored, int red, int green, int blue) {
        sendParticlePacket(flyingItemEntity, pos, stack, colored, ColorUtil.toColor(red, green, blue));
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

    public static void addBehavior(ItemBehavior behavior, Item... items) {
        for (Item item : items) {
            addBehavior(item, behavior);
        }
    }
}
