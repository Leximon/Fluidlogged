package de.leximon.fluidlogged.mixin.classes.worldwarning;

import de.leximon.fluidlogged.Constants;
import de.leximon.fluidlogged.FluidloggedCommon;
import de.leximon.fluidlogged.core.FluidloggedConfig;
import de.leximon.fluidlogged.mixin.interfaces.ILevelInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.worldselection.EditWorldScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.List;

@Mixin(WorldSelectionList.WorldListEntry.class)
public abstract class WorldListWidgetWorldEntryMixin {

    @Shadow @Final private LevelSummary summary;

    @Shadow public abstract void joinWorld();

    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private SelectWorldScreen screen;

    private boolean fl_skipFluidMismatchWarning = false;

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "joinWorld", at = @At("HEAD"), cancellable = true)
    private void injectWarning(CallbackInfo ci) {
        if(fl_skipFluidMismatchWarning)
            return;
        ILevelInfo levelInfo = (ILevelInfo) (Object) summary.getSettings();
        if(levelInfo == null)
            return;
        List<ResourceLocation> levelFluids = levelInfo.fl_getFluidList();
        if(FluidloggedConfig.fluidsLocked.equals(levelFluids))
            return;

        minecraft.setScreen(new BackupConfirmScreen(screen, (backup, eraseCache) -> {
                if (backup) {
                    String levelName = this.summary.getLevelId();
                    try (LevelStorageSource.LevelStorageAccess session = this.minecraft.getLevelSource().createAccess(levelName)) {
                        EditWorldScreen.makeBackupAndShowToast(session);
                    } catch (IOException var9) {
                        SystemToast.onWorldAccessFailure(this.minecraft, levelName);
                        Constants.LOGGER.error("Failed to backup level {}", levelName, var9);
                    }
                }

                fl_skipFluidMismatchWarning = true; // used show other warnings
                joinWorld();
        }, new TranslatableComponent("fluidlogged.fluidMismatchWarning.title"), new TranslatableComponent("fluidlogged.fluidMismatchWarning.description"), false));
        ci.cancel();
    }

}
