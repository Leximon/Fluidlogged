package de.leximon.fluidlogged.mixin.classes;

import de.leximon.fluidlogged.mixin.extensions.RenderChunkExtension;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderChunkRegion.class)
public abstract class RenderChunkRegionMixin {
    
    @Shadow protected abstract RenderChunk getChunk(int x, int z);

    @Inject(
            method = "getFluidState",
            at = @At("HEAD"),
            cancellable = true
    )
    public void getFluidState(BlockPos blockPos, CallbackInfoReturnable<FluidState> cir) {
        int x = SectionPos.blockToSectionCoord(blockPos.getX());
        int z = SectionPos.blockToSectionCoord(blockPos.getZ());
        cir.setReturnValue(((RenderChunkExtension) getChunk(x, z)).getFluidState(blockPos));
    }

}
