package de.leximon.fluidlogged.mixin.classes.fabric.lithium_compat;

import de.leximon.fluidlogged.mixin.extensions.LevelChunkSectionExtension;
import me.jellysquid.mods.lithium.common.block.BlockStateFlags;
import me.jellysquid.mods.lithium.common.block.TrackedBlockStatePredicate;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunkSection.class)
public class LevelChunkSectionMixin {

    @SuppressWarnings({"UnresolvedMixinReference", "MixinAnnotationTarget"})
    @Inject(method = "anyMatch", at = @At("HEAD"), remap = false, cancellable = true)
    private void injectFluidCheck(TrackedBlockStatePredicate trackedBlockStatePredicate, boolean fallback, CallbackInfoReturnable<Boolean> cir) {
        if (!((LevelChunkSectionExtension) this).getFluidStates().isEmpty()
                && (trackedBlockStatePredicate == BlockStateFlags.LAVA || trackedBlockStatePredicate == BlockStateFlags.WATER))
            cir.setReturnValue(true);
    }

}
