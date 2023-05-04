package me.melontini.tweaks.networks;

import me.melontini.crackerutil.client.util.ScreenParticleHelper;
import me.melontini.crackerutil.util.ColorUtil;
import me.melontini.crackerutil.util.MathStuff;
import me.melontini.crackerutil.util.Utilities;
import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.client.particles.screen.DyeParticle;
import me.melontini.tweaks.client.sound.PersistentMovingSoundInstance;
import me.melontini.tweaks.util.TweaksLog;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.ParticlesMode;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static me.melontini.tweaks.Tweaks.MODID;

public class ClientSideNetworking {

    public static Map<UUID, SoundInstance> soundInstanceMap = new ConcurrentHashMap<>();

    public static void register() {
        if (Tweaks.CONFIG.newMinecarts.isJukeboxMinecartOn) {
            ClientPlayNetworking.registerGlobalReceiver(TweaksPackets.JUKEBOX_MINECART_START_PLAYING, (client, handler, buf, responseSender) -> {
                UUID id = buf.readUuid();
                ItemStack stack = buf.readItemStack();
                client.execute(() -> {
                    assert client.world != null;
                    Entity entity = client.world.getEntityLookup().get(id);
                    if (stack.getItem() instanceof MusicDiscItem disc) {
                        var discName = disc.getDescription();
                        soundInstanceMap.computeIfAbsent(id, k -> {
                            SoundInstance instance = new PersistentMovingSoundInstance(disc.getSound(), SoundCategory.RECORDS, id, client.world, Random.create());
                            client.getSoundManager().play(instance);
                            return instance;
                        });
                        if (discName != null)
                            if (client.player != null) if (entity != null) if (entity.distanceTo(client.player) < 76) {
                                client.inGameHud.setRecordPlayingOverlay(discName);
                            }
                    }
                });
            });
            ClientPlayNetworking.registerGlobalReceiver(TweaksPackets.JUKEBOX_MINECART_STOP_PLAYING, (client, handler, buf, responseSender) -> {
                UUID id = buf.readUuid();
                client.execute(() -> {
                    SoundInstance instance = soundInstanceMap.remove(id);
                    if (client.getSoundManager().isPlaying(instance)) {
                        client.getSoundManager().stop(instance);
                    }
                });
            });
        }

        ClientPlayNetworking.registerGlobalReceiver(TweaksPackets.USED_CUSTOM_TOTEM, (client, handler, buf, responseSender) -> {
            UUID id = buf.readUuid();
            ItemStack stack = buf.readItemStack();
            DefaultParticleType particle = (DefaultParticleType) buf.readRegistryValue(Registry.PARTICLE_TYPE);
            client.execute(() -> {
                Entity entity = client.world.getEntityLookup().get(id);
                client.particleManager.addEmitter(entity, particle, 30);
                client.world.playSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_TOTEM_USE, entity.getSoundCategory(), 1.0F, 1.0F, false);
                if (entity == client.player) {
                    client.gameRenderer.showFloatingItem(stack);
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(TweaksPackets.ADD_ONE_PARTICLE, (client, handler, packetByteBuf, responseSender) -> {
            DefaultParticleType particle = (DefaultParticleType) packetByteBuf.readRegistryValue(Registry.PARTICLE_TYPE);
            double x = packetByteBuf.readDouble(), y = packetByteBuf.readDouble(), z = packetByteBuf.readDouble();
            double velocityX = packetByteBuf.readDouble(), velocityY = packetByteBuf.readDouble(), velocityZ = packetByteBuf.readDouble();
            client.execute(() -> {
                assert particle != null;
                client.worldRenderer.addParticle(particle, particle.shouldAlwaysSpawn(), x, y, z, velocityX, velocityY, velocityZ);
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(new Identifier(MODID, "notify_client_about_stuff_please"), (client, handler, packetByteBuf, responseSender) -> {
            int uuid = packetByteBuf.readVarInt();
            ItemStack stack = packetByteBuf.readItemStack();
            client.execute(() -> {
                ItemEntity entity = (ItemEntity) client.world.getEntityLookup().get(uuid);
                if (entity != null) entity.getDataTracker().set(ItemEntity.STACK, stack);
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(TweaksPackets.FLYING_STACK_LANDED, (client, handler, buf, responseSender) -> {
            double x = buf.readDouble(), y = buf.readDouble(), z = buf.readDouble();
            boolean spawnItem = buf.readBoolean();
            ItemStack stack = buf.readItemStack();
            boolean spawnColor = buf.readBoolean();

            int color = 0;
            if (spawnColor) {
                color = buf.readVarInt();
            }

            float r = ColorUtil.getRedF(color);
            float g = ColorUtil.getGreenF(color);
            float b = ColorUtil.getBlueF(color);
            client.execute(() -> {
                ParticlesMode particlesMode = MinecraftClient.getInstance().options.getParticles().getValue();
                if (particlesMode == ParticlesMode.MINIMAL) return;

                if (spawnItem) {
                    for (int i = 0; i < (particlesMode != ParticlesMode.DECREASED ? 8 : 4); ++i) {
                        (MinecraftClient.getInstance()).particleManager.addParticle(
                                new ItemStackParticleEffect(ParticleTypes.ITEM, stack),
                                x, y, z,
                                Utilities.RANDOM.nextGaussian() * 0.15,
                                Utilities.RANDOM.nextDouble() * 0.2,
                                Utilities.RANDOM.nextGaussian() * 0.15
                        );
                    }
                }

                if (spawnColor) {
                    for (int i = 0; i < (particlesMode != ParticlesMode.DECREASED ? 15 : 7); i++) {
                        Particle particle = (MinecraftClient.getInstance()).particleManager.addParticle(ParticleTypes.EFFECT, x, y, z, Utilities.RANDOM.nextGaussian() * 0.15, 0.5, Utilities.RANDOM.nextGaussian() * 0.15);
                        if (particle != null) {
                            particle.setColor(r, g, b);
                        }
                    }
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(TweaksPackets.COLORED_FLYING_STACK_LANDED, (client, handler, buf, responseSender) -> {
            ItemStack dye = buf.readItemStack();
            client.execute(() -> {
                int a = client.getWindow().getScaledWidth();
                ScreenParticleHelper.addParticle(new DyeParticle(MathStuff.nextDouble(Utilities.RANDOM, a/2d - (a/3d), a/2d + a/3d),client.getWindow().getScaledHeight()/2d,0,0, dye));
            });
        });
        TweaksLog.devInfo("ClientSideNetworking init complete!");
    }
}
