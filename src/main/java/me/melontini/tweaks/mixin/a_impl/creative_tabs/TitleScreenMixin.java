package me.melontini.tweaks.mixin.a_impl.creative_tabs;

import me.melontini.tweaks.ducks.ItemGroupAccess;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At(value = "TAIL"), method = "<init>(Z)V")
    private void mTweaks$init(CallbackInfo ci) {
        for (ItemGroup group : ItemGroup.GROUPS) {
            ((ItemGroupAccess)group).mTweaks$initItems();
        }
    }
}
