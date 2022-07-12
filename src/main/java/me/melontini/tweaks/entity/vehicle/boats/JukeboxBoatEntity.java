package me.melontini.tweaks.entity.vehicle.boats;

import me.melontini.tweaks.registries.EntityTypeRegistry;
import me.melontini.tweaks.util.ItemStackUtil;
import me.melontini.tweaks.util.LogUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Clearable;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import static me.melontini.tweaks.Tweaks.MODID;

public class JukeboxBoatEntity extends BoatEntityWithBlock implements Clearable {

    public ItemStack record = ItemStack.EMPTY;

    public JukeboxBoatEntity(EntityType<? extends BoatEntity> entityType, World world) {
        super(entityType, world);
    }

    public JukeboxBoatEntity(World world, double x, double y, double z) {
        this(EntityTypeRegistry.BOAT_WITH_JUKEBOX, world);
        this.setPosition(x, y, z);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!this.world.isClient && this.isAlive()) {
            this.setDamageWobbleSide(-this.getDamageWobbleSide());
            this.setDamageWobbleTicks(10);
            this.setDamageWobbleStrength(this.getDamageWobbleStrength() + amount * 10.0F);
            this.scheduleVelocityUpdate();
            boolean bl = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity) source.getAttacker()).isCreative();
            if (bl || this.getDamageWobbleStrength() > 40.0F) {
                this.stopPlaying();
                if (!bl && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                    this.dropItem(this.asItem());
                }

                this.remove();
            }

            return true;
        } else {
            return true;
        }
    }

    @Override
    public void kill() {
        this.stopPlaying();
        this.remove();
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        ItemStack stackInHand = player.getStackInHand(hand);
        if (!world.isClient())
            if (!this.record.isEmpty() && player.isSneaking()) {
                ItemStackUtil.spawnItemWithRandVelocity(
                        new Vec3d(this.getX(), this.getY() + 0.5, this.getZ()), this.record, this.world, 0.5);
                this.stopPlaying();
                this.clear();
                return ActionResult.SUCCESS;
            } else if (stackInHand.getItem() instanceof MusicDiscItem && record.isEmpty()) {
                this.record = stackInHand.copy();
                this.startPlaying();
                stackInHand.decrement(1);
                player.incrementStat(Stats.PLAY_RECORD);
                return ActionResult.SUCCESS;
            }
        super.interact(player, hand);
        return ActionResult.success(this.world.isClient);
    }

    public void stopPlaying() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(this.getEntityId());

        for (PlayerEntity player1 : world.getPlayers()) {
            ServerPlayNetworking.send((ServerPlayerEntity) player1, new Identifier(MODID, "jukebox_minecart_audio_stop"), buf);
        }
    }

    public void startPlaying() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(this.getEntityId());
        buf.writeItemStack(this.record);

        for (PlayerEntity player1 : world.getPlayers()) {
            ServerPlayNetworking.send((ServerPlayerEntity) player1, new Identifier(MODID, "jukebox_minecart_audio"), buf);
        }
        LogUtil.info(this.record);
    }

    @Override
    public Item asItem() {
        return Registry.ITEM.get(Identifier.tryParse("m-tweaks:" + this.getBoatType().getName() + "_boat_with_jukebox"));
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
    public void clear() {
        this.record = ItemStack.EMPTY;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
