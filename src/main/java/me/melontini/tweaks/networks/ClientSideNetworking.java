package me.melontini.tweaks.networks;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.client.sound.PersistentMovingSoundInstance;
import me.melontini.tweaks.registries.ItemRegistry;
import me.melontini.tweaks.util.LogUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.melontini.tweaks.Tweaks.MODID;

public class ClientSideNetworking {

    public static Map<UUID, SoundInstance> soundInstanceMap = new HashMap<>();

    public static void register() {
        if (Tweaks.CONFIG.newMinecarts.isJukeboxMinecartOn) {
            ClientPlayNetworking.registerGlobalReceiver(new Identifier(MODID, "jukebox_minecart_audio"), (client, handler, buf, responseSender) -> {
                UUID id = buf.readUuid();
                ItemStack stack = buf.readItemStack();
                client.execute(() -> {
                    assert client.world != null;
                    Entity entity = client.world.getEntityLookup().get(id);
                    if (stack.getItem() instanceof MusicDiscItem disc) {

                        var discName = disc.getDescription();
                        SoundInstance instance = new PersistentMovingSoundInstance(disc.getSound(), SoundCategory.RECORDS, id, client.world, Random.create());
                        soundInstanceMap.put(id, instance);
                        client.getSoundManager().play(instance);

                        if (discName != null)
                            if (client.player != null) if (entity != null) if (entity.distanceTo(client.player) < 76) {
                                client.inGameHud.setRecordPlayingOverlay(discName);
                            }
                    }
                });
            });
        }
        if (Tweaks.CONFIG.newMinecarts.isJukeboxMinecartOn) {
            ClientPlayNetworking.registerGlobalReceiver(new Identifier(MODID, "jukebox_minecart_audio_stop"), (client, handler, buf, responseSender) -> {
                UUID id = buf.readUuid();
                client.execute(() -> {
                    SoundInstance instance = soundInstanceMap.get(id);
                    if (client.getSoundManager().isPlaying(instance)) {
                        client.getSoundManager().stop(instance);
                        soundInstanceMap.remove(id);
                        LogUtil.info("removed jbmc sound instance");
                    }
                });
            });
        }

        if (Tweaks.CONFIG.totemSettings.enableInfiniteTotem)
            ClientPlayNetworking.registerGlobalReceiver(new Identifier(MODID, "infinite_totem_use"), (client, handler, buf, responseSender) -> {
                UUID id = buf.readUuid();
                client.execute(() -> {
                    Entity entity = client.world.getEntityLookup().get(id);
                    client.particleManager.addEmitter(entity, Tweaks.KNOCKOFF_TOTEM_PARTICLE, 30);
                    client.world.playSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_TOTEM_USE, entity.getSoundCategory(), 1.0F, 1.0F, false);
                    if (entity == client.player) {
                        client.gameRenderer.showFloatingItem(new ItemStack(ItemRegistry.INFINITE_TOTEM));
                    }
                });
            });

        LogUtil.info("ClientSideNetworking init complete!");
    }
}
