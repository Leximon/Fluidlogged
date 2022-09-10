package de.leximon.fluidlogged.mixin.classes.worldwarning;

import de.leximon.fluidlogged.Fluidlogged;
import de.leximon.fluidlogged.core.FluidloggedConfig;
import de.leximon.fluidlogged.mixin.interfaces.ILevelInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.BackupPromptScreen;
import net.minecraft.client.gui.screen.world.EditWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.List;

@Mixin(WorldListWidget.WorldEntry.class)
public abstract class WorldListWidgetWorldEntryMixin {

    @Shadow @Final private LevelSummary level;

    @Shadow public abstract void play();

    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private SelectWorldScreen screen;

    private boolean fl_skipFluidMismatchWarning = false;

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "play", at = @At("HEAD"), cancellable = true)
    private void injectWarning(CallbackInfo ci) {
        if(fl_skipFluidMismatchWarning)
            return;
        ILevelInfo levelInfo = (ILevelInfo) (Object) level.getLevelInfo();
        if(levelInfo == null)
            return;
        List<Identifier> levelFluids = levelInfo.fl_getFluidList();
        if(FluidloggedConfig.fluidsLocked.equals(levelFluids))
            return;

        client.setScreen(new BackupPromptScreen(screen, (backup, eraseCache) -> {
                if (backup) {
                    String levelName = this.level.getName();
                    try (LevelStorage.Session session = this.client.getLevelStorage().createSession(levelName)) {
                        EditWorldScreen.backupLevel(session);
                    } catch (IOException var9) {
                        SystemToast.addWorldAccessFailureToast(this.client, levelName);
                        Fluidlogged.LOGGER.error("Failed to backup level {}", levelName, var9);
                    }
                }

                fl_skipFluidMismatchWarning = true; // used show other warnings
                play();
        }, Text.translatable("fluidlogged.fluidMismatchWarning.title"), Text.translatable("fluidlogged.fluidMismatchWarning.description"), false));
        ci.cancel();
    }

}
