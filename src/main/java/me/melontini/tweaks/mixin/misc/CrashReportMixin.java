package me.melontini.tweaks.mixin.misc;

import me.melontini.crackerutil.analytics.Analytics;
import me.melontini.crackerutil.analytics.mixpanel.MixpanelAnalytics;
import me.melontini.crackerutil.util.Utilities;
import me.melontini.tweaks.Tweaks;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.crash.CrashReport;
import org.json.JSONArray;
import org.json.JSONObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Base64;
import java.util.List;

@Mixin(MinecraftClient.class)
public abstract class CrashReportMixin {
    private static final MixpanelAnalytics.Handler MTWEAKS_HANDLER = MixpanelAnalytics.init(new String(Base64.getDecoder().decode("NGQ3YWVhZGRjN2M5M2JkNzhiODRmNDViZWI3Y2NlOTE=")), true);

    @Inject(at = @At(value = "HEAD"), method = "printCrashReport")
    private static void mTweaks$uploadCrashReport(CrashReport report, CallbackInfo ci) {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment() && Tweaks.CONFIG.sendOptionalData) {
            List<String> trace = report.getCauseAsString().lines().toList();

            boolean upload = Utilities.process(trace, strings -> {
                for (String string : strings) if (string.contains("me.melontini.tweaks.")) return true;
                return false;
            });

            if (upload) {
                MTWEAKS_HANDLER.send(messageBuilder -> {
                    JSONObject object = new JSONObject();

                    //fill trace.
                    JSONArray stackTrace = new JSONArray();
                    for (String string : trace) stackTrace.put(string);
                    object.put("stackTrace", stackTrace);

                    //fill loaded mods.
                    JSONArray mods = new JSONArray();
                    for (ModContainer mod : FabricLoader.getInstance().getAllMods())
                        if (!mod.getMetadata().getId().startsWith("fabric-")) mods.put(mod.getMetadata().getId() + " (" + mod.getMetadata().getVersion().getFriendlyString() + ")");
                    object.put("mods", mods);

                    return messageBuilder.event(Analytics.getUUIDString(), "Crash", object);
                }, true);
            }
        }
    }
}
