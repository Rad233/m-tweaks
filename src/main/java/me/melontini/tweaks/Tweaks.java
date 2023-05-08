package me.melontini.tweaks;

import com.google.gson.Gson;
import me.melontini.crackerutil.analytics.Analytics;
import me.melontini.crackerutil.analytics.Prop;
import me.melontini.crackerutil.analytics.mixpanel.MixpanelAnalytics;
import me.melontini.crackerutil.util.TextUtil;
import me.melontini.crackerutil.util.mixin.ExtendedPlugin;
import me.melontini.tweaks.config.TweaksConfig;
import me.melontini.tweaks.networks.ServerSideNetworking;
import me.melontini.tweaks.registries.BlockRegistry;
import me.melontini.tweaks.registries.EntityTypeRegistry;
import me.melontini.tweaks.registries.ItemRegistry;
import me.melontini.tweaks.registries.ResourceConditionRegistry;
import me.melontini.tweaks.screens.FletchingScreenHandler;
import me.melontini.tweaks.util.DamageCommand;
import me.melontini.tweaks.util.ItemBehaviorManager;
import me.melontini.tweaks.util.MiscUtil;
import me.melontini.tweaks.util.WorldUtil;
import me.melontini.tweaks.util.data.EggProcessingData;
import me.melontini.tweaks.util.data.PlantData;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Tweaks implements ModInitializer {
    private static final MixpanelAnalytics.Handler HANDLER = MixpanelAnalytics.init(new String(Base64.getDecoder().decode("NGQ3YWVhZGRjN2M5M2JkNzhiODRmNDViZWI3Y2NlOTE=")), true);
    public static final String MODID = "m-tweaks";
    public static EntityAttributeModifier LEAF_SLOWNESS;
    public static TweaksConfig CONFIG = AutoConfig.getConfigHolder(TweaksConfig.class).getConfig();
    public static ScreenHandlerType<FletchingScreenHandler> FLETCHING_SCREEN_HANDLER;
    public static Map<Block, PlantData> PLANT_DATA = new HashMap<>();
    public static Map<Item, EggProcessingData> EGG_DATA = new HashMap<>();
    public static DefaultParticleType KNOCKOFF_TOTEM_PARTICLE;
    public static final DamageSource AGONY = new DamageSource("m_tweaks_agony");
    public static final Map<PlayerEntity, AbstractMinecartEntity> LINKING_CARTS = new HashMap<>();
    public static final Map<PlayerEntity, AbstractMinecartEntity> UNLINKING_CARTS = new HashMap<>();
    public static MinecraftServer SERVER;

    public static DamageSource bricked(@Nullable Entity attacker) {
        return new BrickedDamageSource(attacker);
    }

    private static void stripNonBooleans(JSONObject object) {
        for (String s : new HashSet<>(object.keySet())) {
            try {
                stripNonBooleans(object.getJSONObject(s));
            } catch (Exception ignored) {
                try {
                    object.getBoolean(s);
                } catch (Exception ignored2) {
                    object.remove(s);
                }
            }
        }
    }

    private static void sendConfig(Gson gson) {
        HANDLER.send(messageBuilder -> {
            JSONObject object = new JSONObject();
            JSONObject config = new JSONObject(gson.toJson(CONFIG));
            stripNonBooleans(config);
            object.put("config", config);
            return messageBuilder.event(Analytics.getUUIDString(), "Config", object);
        });
    }

    @Override
    public void onInitialize() {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
            if (CONFIG.sendOptionalData) {
                HANDLER.send(messageBuilder -> {
                    JSONObject object = new JSONObject();
                    object.put("mod_version", FabricLoader.getInstance().getModContainer(MODID).get().getMetadata().getVersion().getFriendlyString());
                    object.put("mc_version", ExtendedPlugin.parseMCVersion().getFriendlyString());
                    return messageBuilder.set(Analytics.getUUIDString(), MixpanelAnalytics.attachProps(object, Prop.ENVIRONMENT));
                });

                Gson gson = new Gson();
                Path fakeConfig = FabricLoader.getInstance().getGameDir().resolve(".m_tweaks/config_copy.json");
                String currentConfig = gson.toJson(CONFIG);
                if (!Files.exists(fakeConfig)) {
                    try {
                        Files.createDirectories(fakeConfig.getParent());
                        Files.write(fakeConfig, currentConfig.getBytes());
                        sendConfig(gson);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        String config = new String(Files.readAllBytes(fakeConfig));
                        if (!config.equals(currentConfig)) {
                            try {
                                Files.write(fakeConfig, currentConfig.getBytes());
                                sendConfig(gson);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                HANDLER.send(messageBuilder -> messageBuilder.delete(Analytics.getUUIDString()));
            }
        }

        BlockRegistry.register();
        ItemRegistry.register();
        EntityTypeRegistry.register();
        ServerSideNetworking.register();
        ResourceConditionRegistry.register();

        LEAF_SLOWNESS = new EntityAttributeModifier(UUID.fromString("f72625eb-d4c4-4e1d-8e5c-1736b9bab349"), "Leaf Slowness", -0.3, EntityAttributeModifier.Operation.MULTIPLY_BASE);
        KNOCKOFF_TOTEM_PARTICLE = FabricParticleTypes.simple();

        if (CONFIG.usefulFletching) {
            FLETCHING_SCREEN_HANDLER = new ScreenHandlerType<>(FletchingScreenHandler::new);
            Registry.register(Registry.SCREEN_HANDLER, new Identifier(MODID, "fletching"), FLETCHING_SCREEN_HANDLER);
        }

        Registry.register(Registry.PARTICLE_TYPE, new Identifier(MODID, "knockoff_totem_particles"), KNOCKOFF_TOTEM_PARTICLE);

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            SERVER = server;
        });

        ServerWorldEvents.LOAD.register((server, world) -> {
            if (CONFIG.tradingGoatHorn) if (world.getRegistryKey() == World.OVERWORLD)
                WorldUtil.getTraderManager(world);
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            Tweaks.PLANT_DATA.clear();
            Tweaks.EGG_DATA.clear();
            if (CONFIG.tradingGoatHorn) {
                ServerWorld world = server.getWorld(World.OVERWORLD);
                if (world != null) {
                    var manager = world.getPersistentStateManager();
                    if (manager.loadedStates.containsKey("mt_trader_statemanager"))
                        WorldUtil.getTraderManager(world).markDirty();
                }
            }
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            Tweaks.LINKING_CARTS.clear();
            Tweaks.UNLINKING_CARTS.clear();
            ItemBehaviorManager.clear();
        });

        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (CONFIG.tradingGoatHorn) if (world.getRegistryKey() == World.OVERWORLD) {
                var manager = world.getPersistentStateManager();
                if (manager.loadedStates.containsKey("mt_trader_statemanager"))
                    WorldUtil.getTraderManager(world).tick();
            }
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            MiscUtil.generateRecipeAdvancements(server);
            server.getPlayerManager().getPlayerList().forEach(entity -> server.getPlayerManager().getAdvancementTracker(entity).reload(server.getAdvancementLoader()));
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            if (CONFIG.damageBackport) DamageCommand.register(dispatcher);
        });
    }

    private static class BrickedDamageSource extends DamageSource {
        private final Entity attacker;

        public BrickedDamageSource(Entity attacker) {
            super("m_tweaks_bricked");
            this.attacker = attacker;
        }

        @Override
        public Text getDeathMessage(LivingEntity entity) {
            if (attacker != null)
                return TextUtil.translatable("death.attack.m_tweaks_bricked.entity", entity.getDisplayName(), attacker.getDisplayName());
            else return TextUtil.translatable("death.attack.m_tweaks_bricked", entity.getDisplayName());
        }
    }

}
