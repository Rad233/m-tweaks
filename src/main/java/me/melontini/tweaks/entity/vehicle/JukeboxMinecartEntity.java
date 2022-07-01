package me.melontini.tweaks.entity.vehicle;

import com.chocohead.mm.api.ClassTinkerers;
import me.melontini.tweaks.registries.EntityTypeRegistry;
import me.melontini.tweaks.registries.ItemRegistry;
import me.melontini.tweaks.util.ItemStackUtil;
import me.melontini.tweaks.util.LogUtil;
import me.melontini.tweaks.util.PlayerUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Clearable;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import static me.melontini.tweaks.Tweaks.MODID;


public class JukeboxMinecartEntity extends AbstractMinecartEntity implements Clearable {
    public ItemStack record = ItemStack.EMPTY;

    public JukeboxMinecartEntity(EntityType<? extends JukeboxMinecartEntity> entityType, World world) {
        super(entityType, world);
    }

    public JukeboxMinecartEntity(World world, double x, double y, double z) {
        super(EntityTypeRegistry.JUKEBOX_MINECART_ENTITY, world, x, y, z);
    }

    @Override
    public Type getMinecartType() {
        return /*who asked?*/ ClassTinkerers.getEnum(Type.class, "M_TWEAKS_JUKEBOX");
    }

    @Override
    public void onActivatorRail(int x, int y, int z, boolean powered) {
        if (powered && !this.record.isEmpty()) {
            ItemStackUtil.spawnItemWithRandVelocity(
                    new Vec3d(this.getX(), this.getY() + 0.5, this.getZ()), this.record, this.world);
            this.clear();
            this.stopPlaying();
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.world.isClient || this.isRemoved()) {
            return true;
        } else if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            this.setDamageWobbleSide(-this.getDamageWobbleSide());
            this.setDamageWobbleTicks(10);
            this.scheduleVelocityUpdate();
            this.setDamageWobbleStrength(this.getDamageWobbleStrength() + amount * 10.0F);
            this.emitGameEvent(GameEvent.ENTITY_DAMAGED, source.getAttacker());
            boolean isCreativePlayer = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity) source.getAttacker()).getAbilities().creativeMode;
            if (isCreativePlayer || this.getDamageWobbleStrength() > 40.0F) {
                this.removeAllPassengers();
                this.stopPlaying();
                if (isCreativePlayer && !this.hasCustomName()) {
                    this.discard();
                } else {
                    this.dropItems(source);
                }
            }

            return true;
        }
    }

    @Override
    public void dropItems(DamageSource damageSource) {
        super.dropItems(damageSource);
        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            this.dropItem(Blocks.JUKEBOX);
            this.dropItem(record.getItem());
        }
    }

    @Override
    public void kill() {
        this.stopPlaying();
        this.remove(Entity.RemovalReason.KILLED);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        ItemStack stackInHand = player.getStackInHand(hand);
        if (!world.isClient())
            if (!this.record.isEmpty()) {
                ItemStackUtil.spawnItemWithRandVelocity(
                        new Vec3d(this.getX(), this.getY() + 0.5, this.getZ()), this.record, this.world);
                this.stopPlaying();
                this.clear();
            } else if (stackInHand.getItem() instanceof MusicDiscItem) {
                this.record = stackInHand.copy();
                this.startPlaying();
                stackInHand.decrement(1);
                player.incrementStat(Stats.PLAY_RECORD);
            }
        return ActionResult.success(this.world.isClient);
    }

    public void stopPlaying() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(this.getUuid());

        for (PlayerEntity player1 : world.getPlayers()) {
            ServerPlayNetworking.send((ServerPlayerEntity) player1, new Identifier(MODID, "jukebox_minecart_audio_stop"), buf);
        }
    }

    public void startPlaying() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(this.uuid);
        buf.writeItemStack(this.record);

        for (PlayerEntity player1 : world.getPlayers()) {
            ServerPlayNetworking.send((ServerPlayerEntity) player1, new Identifier(MODID, "jukebox_minecart_audio"), buf);
        }
        LogUtil.info(this.record);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("Items", 10)) {
            this.record = (ItemStack.fromNbt(nbt.getCompound("Items")));
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (!this.record.isEmpty())
            nbt.put("Items", this.record.writeNbt(new NbtCompound()));
    }

    @Override
    public BlockState getDefaultContainedBlock() {
        return Blocks.JUKEBOX.getDefaultState();
    }

    @Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(ItemRegistry.JUKEBOX_MINECART);
    }

    @Override
    public void clear() {
        this.record = ItemStack.EMPTY;
    }
}
