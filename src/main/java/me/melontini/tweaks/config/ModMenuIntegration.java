package me.melontini.tweaks.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.melontini.tweaks.client.TweaksClient;
import me.melontini.tweaks.mixin.accessors.ScreenAccessor;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {//TODO, maybe, make my own config screen
        return parent -> {
            Screen c = AutoConfig.getConfigScreen(TweaksConfig.class, parent).get();
            ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
                if (screen == c) {
                    ((ScreenAccessor) screen).mTweaks$addDrawableChild(new TexturedButtonWidget(screen.width - 40, 13, 20, 20, 0, 0, 20, TweaksClient.WIKI_BUTTON_TEXTURE, 32, 64, button -> screen.handleTextClick(TweaksClient.EVENT)));
                }
            });
            return c;
        };
    }
}
