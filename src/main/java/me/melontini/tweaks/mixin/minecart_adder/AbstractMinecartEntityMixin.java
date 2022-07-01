package me.melontini.tweaks.mixin.minecart_adder;

import com.chocohead.mm.api.ClassTinkerers;
import me.melontini.tweaks.config.TweaksConfig;
import me.melontini.tweaks.entity.vehicle.AnvilMinecartEntity;
import me.melontini.tweaks.entity.vehicle.JukeboxMinecartEntity;
import me.melontini.tweaks.entity.vehicle.NoteBlockMinecartEntity;
import me.shedaniel.autoconfig.AutoConfig;
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
        TweaksConfig config = AutoConfig.getConfigHolder(TweaksConfig.class).getConfig();

        if (type == ClassTinkerers.getEnum(AbstractMinecartEntity.Type.class, "M_TWEAKS_ANVIL") && config.newMinecarts.isAnvilMinecartOn) {
            cir.setReturnValue(new AnvilMinecartEntity(world, x, y, z));
        } else if (type == ClassTinkerers.getEnum(AbstractMinecartEntity.Type.class, "M_TWEAKS_NOTEBLOCK") && config.newMinecarts.isNoteBlockMinecartOn) {
            cir.setReturnValue(new NoteBlockMinecartEntity(world, x, y, z));
        } else if (type == ClassTinkerers.getEnum(AbstractMinecartEntity.Type.class, "M_TWEAKS_JUKEBOX") && config.newMinecarts.isJukeboxMinecartOn) {
            cir.setReturnValue(new JukeboxMinecartEntity(world, x, y, z));
        }
    }
}
