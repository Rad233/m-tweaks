package me.melontini.tweaks.networks;

import me.melontini.tweaks.client.sound.JukeboxMinecartSoundInstance;
import me.melontini.tweaks.config.TweaksConfig;
import me.melontini.tweaks.entity.vehicle.JukeboxMinecartEntity;
import me.melontini.tweaks.util.LogUtil;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.melontini.tweaks.Tweaks.MODID;

public class ClientSideNetworking {

    public static Map<UUID, SoundInstance> soundInstanceMap;

    public static void register() {
        TweaksConfig config = AutoConfig.getConfigHolder(TweaksConfig.class).getConfig();

        soundInstanceMap = new HashMap<>();
        if (config.newMinecarts.isJukeboxMinecartOn) {
            ClientPlayNetworking.registerGlobalReceiver(new Identifier(MODID, "jukebox_minecart_audio"), (client, handler, buf, responseSender) -> {
                UUID id = buf.readUuid();
                ItemStack stack = buf.readItemStack();
                client.execute(() -> {
                    LogUtil.info(stack);
                    assert client.world != null;
                    JukeboxMinecartEntity entity = (JukeboxMinecartEntity) client.world.getEntityLookup().get(id);
                    LogUtil.info("started executing task on client");
                    //null check
                    if (stack.getItem() instanceof MusicDiscItem disc) {
                        MutableText discName = disc.getDescription();
                        //that's ridiculous
                        SoundInstance instance = new JukeboxMinecartSoundInstance(disc.getSound(), SoundCategory.RECORDS, id, client.world, client.player);
                        soundInstanceMap.put(id, instance);
                        client.getSoundManager().play(instance);
                        LogUtil.info("added new jbmc sound instance");
                        if (discName != null) if (client.player != null) if (entity != null) if (entity.distanceTo(client.player) < 76) {
                            TranslatableText text = new TranslatableText("record.nowPlaying", discName);
                            client.player.sendMessage(new TranslatableText("record.nowPlaying", discName), true);
                        }
                    }
                });
            });
        }
        if (config.newMinecarts.isJukeboxMinecartOn) {
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

        LogUtil.info("ClientSideNetworking init complete!");
    }
}
