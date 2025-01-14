package me.melontini.tweaks.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import me.melontini.tweaks.screens.FletchingScreenHandler;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static me.melontini.tweaks.Tweaks.MODID;

public class FletchingScreen extends ForgingScreen<FletchingScreenHandler> {

    private static final Identifier TEXTURE = new Identifier(MODID, "textures/gui/fletching.png");

    public FletchingScreen(FletchingScreenHandler handler, PlayerInventory playerInventory, Text title) {
        super(handler, playerInventory, title, TEXTURE);
        this.titleX = 60;
        this.titleY = 18;
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        RenderSystem.disableBlend();
        super.drawForeground(matrices, mouseX, mouseY);
    }
}
