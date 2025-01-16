package de.leximon.fluidlogged.mixin.classes.compat.lithium;

import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LevelChunkSection.class)
public class LevelChunkSectionMixin {

//    @SuppressWarnings({"UnresolvedMixinReference", "MixinAnnotationTarget"})
//    @Inject(method = "anyMatch", at = @At("HEAD"), remap = false, cancellable = true)
//    private void injectFluidCheck(TrackedBlockStatePredicate trackedBlockStatePredicate, boolean fallback, CallbackInfoReturnable<Boolean> cir) {
//        if (!((LevelChunkSectionExtension) this).getFluidStates().isEmpty()
//                && (trackedBlockStatePredicate == BlockStateFlags.LAVA || trackedBlockStatePredicate == BlockStateFlags.WATER))
//            cir.setReturnValue(true);
//    }

}
