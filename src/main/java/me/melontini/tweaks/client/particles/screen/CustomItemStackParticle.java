package me.melontini.tweaks.client.particles.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.melontini.crackerutil.client.particles.ItemStackParticle;
import me.melontini.crackerutil.util.Utilities;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class CustomItemStackParticle extends ItemStackParticle {
    private final int seed;
    public CustomItemStackParticle(double x, double y, double velX, double velY, ItemStack stack) {
        super(x, y, velX, velY, stack);
        this.seed = Utilities.RANDOM.nextInt();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        float x = (float) MathHelper.lerp(delta, prevX, this.x);
        float y = (float) MathHelper.lerp(delta, prevY, this.y);
        MatrixStack matrices = RenderSystem.getModelViewStack();
        matrices.push();
        matrices.translate(x, y, 500);
        float angle = (float) Math.toDegrees(Math.atan2(velY, velX) * 0.5);
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(angle));
        RenderSystem.applyModelViewMatrix();
        this.client.getItemRenderer().renderInGuiWithOverrides(this.client.player, this.stack, -8, -8, this.seed);
        this.client.getItemRenderer().renderGuiItemOverlay(this.client.textRenderer, this.stack, -8, -8);
        matrices.pop();
        RenderSystem.applyModelViewMatrix();
    }
}
