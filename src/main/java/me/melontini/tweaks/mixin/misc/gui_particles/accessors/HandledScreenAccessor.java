package me.melontini.tweaks.mixin.misc.gui_particles.accessors;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HandledScreen.class)
public interface HandledScreenAccessor {
    @Accessor("x")
    int mTweaks$getX();

    @Accessor("y")
    int mTweaks$getY();
}
