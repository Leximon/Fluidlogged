package de.leximon.fluidlogged.mixin.classes;

import de.leximon.fluidlogged.mixin.extensions.RenderChunkExtension;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RenderChunkRegion.class)
public class RenderChunkRegionMixin {

    @Shadow @Final protected RenderChunk[][] chunks;
    @Shadow @Final private int centerX;
    @Shadow @Final private int centerZ;


    /**
     * @author Leximon (Fluidlogged)
     * @reason get chunk specific fluid state
     */
    @Overwrite
    public FluidState getFluidState(BlockPos blockPos) {
        int i = SectionPos.blockToSectionCoord(blockPos.getX()) - this.centerX;
        int j = SectionPos.blockToSectionCoord(blockPos.getZ()) - this.centerZ;
        return ((RenderChunkExtension) chunks[i][j]).getFluidState(blockPos);
    }

}
