package me.melontini.tweaks.entity.vehicle.boats;

import me.melontini.tweaks.config.TweaksConfig;
import me.melontini.tweaks.registries.EntityTypeRegistry;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.impl.content.registry.FuelRegistryImpl;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class FurnaceBoatEntity extends BoatEntityWithBlock {
    /*bad idea?*/ private static final TrackedData<Integer> FUEL = DataTracker.registerData(FurnaceBoatEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public FurnaceBoatEntity(EntityType<? extends BoatEntity> entityType, World world) {
        super(entityType, world);
    }

    public FurnaceBoatEntity(World world, double x, double y, double z) {
        this(EntityTypeRegistry.BOAT_WITH_FURNACE, world);
        this.setPosition(x, y, z);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(FUEL, 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getFuel() > 0 && this.world.random.nextInt(4) == 0) {
            this.world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX(), this.getY() + 0.8, this.getZ(), -(this.getVelocity().x * 0.3), 0.08, -(this.getVelocity().z * 0.3));
        }
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        TweaksConfig config = AutoConfig.getConfigHolder(TweaksConfig.class).getConfig();
        ItemStack itemStack = player.getStackInHand(hand);
        if (FuelRegistryImpl.INSTANCE.get(itemStack.getItem()) != null) {
            int itemFuel = FuelRegistryImpl.INSTANCE.get(itemStack.getItem());
            if ((this.getFuel() + (itemFuel * 2.25)) <= config.maxFurnaceMinecartFuel) {
                if (!player.getAbilities().creativeMode) {
                    if (itemStack.getItem().getRecipeRemainder() != null)
                        player.inventory.insertStack(itemStack.getItem().getRecipeRemainder().getDefaultStack());
                    itemStack.decrement(1);
                }

                this.setFuel((int) (getFuel() + (itemFuel * 2.25)));
            }
        }
        super.interact(player, hand);
        return ActionResult.SUCCESS;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("MT-Fuel", this.getFuel());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        setFuel(nbt.getInt("MT-Fuel"));
    }

    @SuppressWarnings({"DuplicateBranchesInSwitch", "UnnecessaryDefault"})
    @Override
    public Item asItem() {
        return switch (this.getBoatType()) {
            case OAK -> Registry.ITEM.get(Identifier.tryParse("m-tweaks:" + Type.OAK.getName() + "_boat_with_furnace"));
            case SPRUCE ->
                    Registry.ITEM.get(Identifier.tryParse("m-tweaks:" + Type.SPRUCE.getName() + "_boat_with_furnace"));
            case BIRCH ->
                    Registry.ITEM.get(Identifier.tryParse("m-tweaks:" + Type.BIRCH.getName() + "_boat_with_furnace"));
            case JUNGLE ->
                    Registry.ITEM.get(Identifier.tryParse("m-tweaks:" + Type.JUNGLE.getName() + "_boat_with_furnace"));
            case ACACIA ->
                    Registry.ITEM.get(Identifier.tryParse("m-tweaks:" + Type.ACACIA.getName() + "_boat_with_furnace"));
            case DARK_OAK ->
                    Registry.ITEM.get(Identifier.tryParse("m-tweaks:" + Type.DARK_OAK.getName() + "_boat_with_furnace"));
            default -> Registry.ITEM.get(Identifier.tryParse("m-tweaks:" + Type.OAK.getName() + "_boat_with_furnace"));
        };
    }

    public int getFuel() {
        return this.dataTracker.get(FUEL);
    }

    public void setFuel(int fuel) {
        this.dataTracker.set(FUEL, fuel);
    }
}
