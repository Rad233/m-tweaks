package me.melontini.tweaks;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Supplier;

import static me.melontini.tweaks.util.MiscUtil.hackAdvancements;

public class Tweaks implements ModInitializer {

    public static final String MODID = "m-tweaks";
    public static final Random RANDOM = new Random();
    public static EntityAttributeModifier LEAF_SLOWNESS;
    public static TweaksConfig CONFIG = AutoConfig.getConfigHolder(TweaksConfig.class).getConfig();
    public static ScreenHandlerType<FletchingScreenHandler> FLETCHING_SCREEN_HANDLER;
    public static Map<Block, PlantData> PLANT_DATA = new HashMap<>();
    public static Map<Item, EggProcessingData> EGG_DATA = new HashMap<>();
    public static DefaultParticleType KNOCKOFF_TOTEM_PARTICLE;
    public static ItemGroup GROUP = ((Supplier<ItemGroup>) () -> {
        ((ItemGroupExtensions) ItemGroup.BUILDING_BLOCKS).fabric_expandArray();
        return new MtGroup(ItemGroup.GROUPS.length - 1, "mt_item_group");
    }).get();

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


        MiscUtil.TYPE_CONSUMER_MAP.put(RecipeType.BLASTING, (map, recipe) -> map.put(new Identifier(recipe.getId().getNamespace(), "recipes/gen/blasting/" + recipe.getId().toString().replace(":", "_")), MiscUtil.createAdvBuilder(recipe.getId(), recipe.getIngredients().get(0))));
        MiscUtil.TYPE_CONSUMER_MAP.put(RecipeType.SMOKING, (map, recipe) -> map.put(new Identifier(recipe.getId().getNamespace(), "recipes/gen/smoking/" + recipe.getId().toString().replace(":", "_")), MiscUtil.createAdvBuilder(recipe.getId(), recipe.getIngredients().get(0))));
        MiscUtil.TYPE_CONSUMER_MAP.put(RecipeType.SMELTING, (map, recipe) -> map.put(new Identifier(recipe.getId().getNamespace(), "recipes/gen/smelting/" + recipe.getId().toString().replace(":", "_")), MiscUtil.createAdvBuilder(recipe.getId(), recipe.getIngredients().get(0))));
        MiscUtil.TYPE_CONSUMER_MAP.put(RecipeType.CAMPFIRE_COOKING, (map, recipe) -> map.put(new Identifier(recipe.getId().getNamespace(), "recipes/gen/campfire_cooking/" + recipe.getId().toString().replace(":", "_")), MiscUtil.createAdvBuilder(recipe.getId(), recipe.getIngredients().get(0))));
        MiscUtil.TYPE_CONSUMER_MAP.put(RecipeType.STONECUTTING, (map, recipe) -> map.put(new Identifier(recipe.getId().getNamespace(), "recipes/gen/stonecutting/" + recipe.getId().toString().replace(":", "_")), MiscUtil.createAdvBuilder(recipe.getId(), recipe.getIngredients().get(0))));
        MiscUtil.TYPE_CONSUMER_MAP.put(RecipeType.CRAFTING, (map, recipe) -> {
            if (!(recipe instanceof SpecialCraftingRecipe)) {
                if (!recipe.getIngredients().isEmpty()) {
                    map.put(new Identifier(recipe.getId().getNamespace(), "recipes/gen/crafting/" + recipe.getId().toString().replace(":", "_")), MiscUtil.createAdvBuilder(recipe.getId(), recipe.getIngredients().toArray(Ingredient[]::new)));
                }
            }
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            if (Tweaks.CONFIG.autogenRecipeAdvancements.autogenRecipeAdvancements) hackAdvancements(server);
        });

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, manager, b) -> {
            if (b) if (Tweaks.CONFIG.autogenRecipeAdvancements.autogenRecipeAdvancements) hackAdvancements(server);
        });
    }

    public static class MtGroup extends ItemGroup implements AnimatedItemGroup {

        public MtGroup(int index, String id) {
            super(index, id);
        }

        @Environment(EnvType.CLIENT)
        @Override
        public void animateIcon(MatrixStack matrixStack, CreativeInventoryScreen screen, int l, int m) {
            MinecraftClient client = MinecraftClient.getInstance();

            float angle = Util.getMeasuringTimeMs() * 0.09f;
            RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            BakedModel model = client.getItemRenderer().getModel(this.getIcon(), null, null, 0);
            MatrixStack stack = RenderSystem.getModelViewStack();
            stack.push();
            stack.translate(l, m, 100.0F + client.getItemRenderer().zOffset);
            stack.translate(8.0, 8.0, 0.0);
            stack.scale(1.0F, -1.0F, 1.0F);
            stack.scale(16.0F, 16.0F, 16.0F);
            stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(angle));
            RenderSystem.applyModelViewMatrix();

            VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
            boolean bl = !model.isSideLit();
            if (bl) {
                DiffuseLighting.disableGuiDepthLighting();
            }

            client.getItemRenderer().renderItem(this.getIcon(), ModelTransformation.Mode.GUI, false, new MatrixStack(), immediate, 15728880, OverlayTexture.DEFAULT_UV, model);
            immediate.draw();
            RenderSystem.enableDepthTest();
            if (bl) {
                DiffuseLighting.enableGuiDepthLighting();
            }

            stack.pop();
            RenderSystem.applyModelViewMatrix();

            client.getItemRenderer().zOffset = 0.0F;
        }

        @Override
        public ItemStack createIcon() {
            return ItemRegistry.INCUBATOR.getDefaultStack();
        }

        private final DefaultedList<ItemStack> EMPTY_LIST = DefaultedList.ofSize(9, ItemStack.EMPTY);
        @Override
        public void appendStacks(DefaultedList<ItemStack> stacks) {
            List<ItemStack> misc = new ArrayList<>();
            if (Tweaks.CONFIG.incubatorSettings.enableIncubator) misc.add(ItemRegistry.INCUBATOR.getDefaultStack());
            if (Tweaks.CONFIG.totemSettings.enableInfiniteTotem) misc.add(ItemRegistry.INFINITE_TOTEM.getDefaultStack());
            appendStacks(stacks, misc);

            List<ItemStack> carts = new ArrayList<>();
            if (Tweaks.CONFIG.newMinecarts.isAnvilMinecartOn) carts.add(ItemRegistry.ANVIL_MINECART.getDefaultStack());
            if (Tweaks.CONFIG.newMinecarts.isJukeboxMinecartOn) carts.add(ItemRegistry.JUKEBOX_MINECART.getDefaultStack());
            if (Tweaks.CONFIG.newMinecarts.isNoteBlockMinecartOn) carts.add(ItemRegistry.NOTE_BLOCK_MINECART.getDefaultStack());
            carts.add(ItemRegistry.SPAWNER_MINECART.getDefaultStack());
            appendStacks(stacks, carts);

            List<ItemStack> boats = new ArrayList<>();
            for (BoatEntity.Type value : BoatEntity.Type.values()) {
                if (Tweaks.CONFIG.newBoats.isFurnaceBoatOn) boats.add(Registry.ITEM.get(new Identifier(MODID, value.getName().replace(":", "_") + "_boat_with_furnace")).getDefaultStack());
                if (Tweaks.CONFIG.newBoats.isJukeboxBoatOn) boats.add(Registry.ITEM.get(new Identifier(MODID, value.getName().replace(":", "_") + "_boat_with_jukebox")).getDefaultStack());
                if (Tweaks.CONFIG.newBoats.isTNTBoatOn) boats.add(Registry.ITEM.get(new Identifier(MODID, value.getName().replace(":", "_") + "_boat_with_tnt")).getDefaultStack());
                if (Tweaks.CONFIG.newBoats.isHopperBoatOn) boats.add(Registry.ITEM.get(new Identifier(MODID, value.getName().replace(":", "_") + "_boat_with_hopper")).getDefaultStack());
            }
            appendStacks(stacks, boats);
        }

        private void appendStacks(DefaultedList<ItemStack> stacks, List<ItemStack> list) {
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
