package me.melontini.tweaks.networks;

import me.melontini.tweaks.Tweaks;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.world.explosion.Explosion;

import java.util.Objects;
import java.util.UUID;

import static me.melontini.tweaks.Tweaks.MODID;

public class ServerSideNetworking {
    public static void register() {
        if (Tweaks.CONFIG.newBoats.isTNTBoatOn)
            ServerPlayNetworking.registerGlobalReceiver(new Identifier(MODID, "boat_explosion_server"), (server, player, handler, buf, responseSender) -> {
                UUID id = buf.readUuid();
                double velocity = buf.readDouble();
                server.execute(() -> {
                    double d = Math.sqrt(velocity);
                    if (d > 5.0) {
                        d = 5.0;
                    }
                    Entity entity = player.world.getEntityLookup().get(id);
                    Objects.requireNonNull(entity, String.format("Server Received Invalid TNT Boat UUID: %s", id)).discard();
                    player.world.createExplosion(entity, entity.getX(), entity.getY(), entity.getZ(), (float) (4.0 + player.world.random.nextDouble() * 1.5 * d), Explosion.DestructionType.DESTROY);
                });
            });
    }
}
