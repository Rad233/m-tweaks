package me.melontini.tweaks.mixin.entities.minecart_adder;

import com.chocohead.mm.api.ClassTinkerers;
import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.entity.vehicle.minecarts.AnvilMinecartEntity;
import me.melontini.tweaks.entity.vehicle.minecarts.JukeboxMinecartEntity;
import me.melontini.tweaks.entity.vehicle.minecarts.NoteBlockMinecartEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractMinecartEntity.class)
public class AbstractMinecartEntityMixin {
    @Inject(at = @At("HEAD"), method = "create", cancellable = true)
    private static void mTweaks$create(World world, double x, double y, double z, AbstractMinecartEntity.Type type, CallbackInfoReturnable<AbstractMinecartEntity> cir) {
        if (type == ClassTinkerers.getEnum(AbstractMinecartEntity.Type.class, "M_TWEAKS_ANVIL") && Tweaks.CONFIG.newMinecarts.isAnvilMinecartOn) {
            cir.setReturnValue(new AnvilMinecartEntity(world, x, y, z));
        } else if (type == ClassTinkerers.getEnum(AbstractMinecartEntity.Type.class, "M_TWEAKS_NOTEBLOCK") && Tweaks.CONFIG.newMinecarts.isNoteBlockMinecartOn) {
            cir.setReturnValue(new NoteBlockMinecartEntity(world, x, y, z));
        } else if (type == ClassTinkerers.getEnum(AbstractMinecartEntity.Type.class, "M_TWEAKS_JUKEBOX") && Tweaks.CONFIG.newMinecarts.isJukeboxMinecartOn) {
            cir.setReturnValue(new JukeboxMinecartEntity(world, x, y, z));
        }
    }
}
