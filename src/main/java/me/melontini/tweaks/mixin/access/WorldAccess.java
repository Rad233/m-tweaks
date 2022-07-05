package me.melontini.tweaks.mixin.access;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityLookup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(World.class)
public interface WorldAccess {
    @Invoker("getEntityLookup")
    EntityLookup<Entity> getEntityLookup();
}
