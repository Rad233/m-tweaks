package me.melontini.tweaks.entity.vehicle.minecarts;

import com.chocohead.mm.api.ClassTinkerers;
import me.melontini.tweaks.registries.EntityTypeRegistry;
import me.melontini.tweaks.registries.ItemRegistry;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class AnvilMinecartEntity extends AbstractMinecartEntity {
    //TODO damage entities on fall.
    public AnvilMinecartEntity(EntityType<? extends AnvilMinecartEntity> entityType, World world) {
        super(entityType, world);
    }

    public AnvilMinecartEntity(World world, double x, double y, double z) {
        super(EntityTypeRegistry.ANVIL_MINECART_ENTITY, world, x, y, z);
    }

    @Override
    public AbstractMinecartEntity.Type getMinecartType() {
        return ClassTinkerers.getEnum(Type.class, "M_TWEAKS_ANVIL");
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        return ActionResult.success(world.isClient);
    }

    public void dropItems(DamageSource damageSource) {
        super.dropItems(damageSource);
        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            this.dropItem(Items.ANVIL);
        }
    }

    @Override
    public double getMaxSpeed() {
        return (this.isTouchingWater() ? 0.08 : 0.1) / 20.0;
    }

    @Override
    public BlockState getDefaultContainedBlock() {
        return Blocks.ANVIL.getDefaultState().with(AnvilBlock.FACING, Direction.NORTH);
    }

    @Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(ItemRegistry.ANVIL_MINECART);
    }
}
