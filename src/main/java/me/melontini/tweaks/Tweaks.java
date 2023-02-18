package me.melontini.tweaks;

import me.melontini.crackerutil.client.util.DrawUtil;
import me.melontini.crackerutil.interfaces.AnimatedItemGroup;
import me.melontini.crackerutil.util.MathStuff;
import me.melontini.tweaks.config.TweaksConfig;
import me.melontini.tweaks.networks.ServerSideNetworking;
import me.melontini.tweaks.registries.BlockRegistry;
import me.melontini.tweaks.registries.EntityTypeRegistry;
import me.melontini.tweaks.registries.ItemRegistry;
import me.melontini.tweaks.registries.ResourceConditionRegistry;
import me.melontini.tweaks.screens.FletchingScreenHandler;
import me.melontini.tweaks.util.MiscUtil;
import me.melontini.tweaks.util.WorldUtil;
import me.melontini.tweaks.util.data.EggProcessingData;
import me.melontini.tweaks.util.data.PlantData;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.impl.item.group.ItemGroupExtensions;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.Pair;
import net.minecraft.world.World;

import java.util.*;

public class Tweaks implements ModInitializer {

    public static final String MODID = "m-tweaks";
    public static final Random RANDOM = new Random();
    public static EntityAttributeModifier LEAF_SLOWNESS;
    public static TweaksConfig CONFIG = AutoConfig.getConfigHolder(TweaksConfig.class).getConfig();
    public static ScreenHandlerType<FletchingScreenHandler> FLETCHING_SCREEN_HANDLER;
    public static Map<Block, PlantData> PLANT_DATA = new HashMap<>();
    public static Map<Item, EggProcessingData> EGG_DATA = new HashMap<>();
    public static DefaultParticleType KNOCKOFF_TOTEM_PARTICLE;
    public static ItemGroup GROUP = Util.make(() -> {
        ((ItemGroupExtensions) ItemGroup.BUILDING_BLOCKS).fabric_expandArray();
        return new MtGroup(ItemGroup.GROUPS.length - 1, "mt_item_group");
    });

    public static final Map<PlayerEntity, AbstractMinecartEntity> LINKING_CARTS = new HashMap<>();
    public static final Map<PlayerEntity, AbstractMinecartEntity> UNLINKING_CARTS = new HashMap<>();

    @Override
    public void onInitialize() {
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

        if (Tweaks.CONFIG.totemSettings.enableInfiniteTotem)
            Registry.register(Registry.PARTICLE_TYPE, new Identifier(MODID, "knockoff_totem_particles"), KNOCKOFF_TOTEM_PARTICLE);

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
    }

    public static class MtGroup extends ItemGroup implements AnimatedItemGroup {

        private final DefaultedList<ItemStack> EMPTY_LIST = DefaultedList.ofSize(9, ItemStack.EMPTY);

        public MtGroup(int index, String id) {
            super(index, id);
        }

        @Environment(EnvType.CLIENT)
        @Override
        public void animateIcon(MatrixStack stack, int l, int m) {
            MinecraftClient client = MinecraftClient.getInstance();

            float angle = Util.getMeasuringTimeMs() * 0.09f;
            stack.push();
            stack.translate(l, m, 100.0F + client.getItemRenderer().zOffset);
            stack.translate(8.0, 8.0, 0.0);
            stack.scale(1.0F, -1.0F, 1.0F);
            stack.scale(16.0F, 16.0F, 16.0F);
            stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(angle));
            BakedModel model = client.getItemRenderer().getModel(this.getIcon(), null, null, 0);
            DrawUtil.renderGuiItemModelCustomMatrixNoTransform(stack, this.getIcon(), model);
            stack.pop();
            //RenderSystem.applyModelViewMatrix();
        }

        @Override
        public ItemStack createIcon() {
            if (Tweaks.CONFIG.unknown) {
                return ItemRegistry.ROSE_OF_THE_VALLEY.getDefaultStack();
            }
            if (Tweaks.CONFIG.incubatorSettings.enableIncubator) {
                return ItemRegistry.INCUBATOR.getDefaultStack();
            }
            return ItemRegistry.SPAWNER_MINECART.getDefaultStack();
        }

        @Override
        public void appendStacks(DefaultedList<ItemStack> stacks) {
            List<ItemStack> misc = new ArrayList<>();
            if (Tweaks.CONFIG.incubatorSettings.enableIncubator) misc.add(ItemRegistry.INCUBATOR.getDefaultStack());
            if (Tweaks.CONFIG.totemSettings.enableInfiniteTotem)
                misc.add(ItemRegistry.INFINITE_TOTEM.getDefaultStack());
            appendStacks(stacks, misc);

            List<ItemStack> carts = new ArrayList<>();
            if (Tweaks.CONFIG.newMinecarts.isAnvilMinecartOn) carts.add(ItemRegistry.ANVIL_MINECART.getDefaultStack());
            if (Tweaks.CONFIG.newMinecarts.isJukeboxMinecartOn)
                carts.add(ItemRegistry.JUKEBOX_MINECART.getDefaultStack());
            if (Tweaks.CONFIG.newMinecarts.isNoteBlockMinecartOn)
                carts.add(ItemRegistry.NOTE_BLOCK_MINECART.getDefaultStack());
            carts.add(ItemRegistry.SPAWNER_MINECART.getDefaultStack());
            appendStacks(stacks, carts);

            List<ItemStack> boats = new ArrayList<>();
            for (BoatEntity.Type value : BoatEntity.Type.values()) {
                if (Tweaks.CONFIG.newBoats.isFurnaceBoatOn)
                    boats.add(Registry.ITEM.get(new Identifier(MODID, value.getName().replace(":", "_") + "_boat_with_furnace")).getDefaultStack());
                if (Tweaks.CONFIG.newBoats.isJukeboxBoatOn)
                    boats.add(Registry.ITEM.get(new Identifier(MODID, value.getName().replace(":", "_") + "_boat_with_jukebox")).getDefaultStack());
                if (Tweaks.CONFIG.newBoats.isTNTBoatOn)
                    boats.add(Registry.ITEM.get(new Identifier(MODID, value.getName().replace(":", "_") + "_boat_with_tnt")).getDefaultStack());
                if (Tweaks.CONFIG.newBoats.isHopperBoatOn)
                    boats.add(Registry.ITEM.get(new Identifier(MODID, value.getName().replace(":", "_") + "_boat_with_hopper")).getDefaultStack());
            }
            appendStacks(stacks, boats);
        }

        private void appendStacks(DefaultedList<ItemStack> stacks, List<ItemStack> list) {
            if (list.isEmpty()) return; //we shouldn't add line breaks if there are no items.

            int rows = MathStuff.fastCeil(list.size() / 9d);
            stacks.addAll(list);
            int left = (rows * 9) - list.size();
            for (int i = 0; i < left; i++) {
                stacks.add(ItemStack.EMPTY); //fill the gaps
            }
            stacks.addAll(EMPTY_LIST); //line break
        }
    }
}
