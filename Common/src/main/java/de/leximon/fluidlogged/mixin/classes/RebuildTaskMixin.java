package de.leximon.fluidlogged.mixin.classes;

import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk$RebuildTask")
public class RebuildTaskMixin {

    @Unique private BlockPos fluidloggedBlockPos;
    @Unique private RenderChunkRegion fluidloggedRenderChunkRegion;

    @Redirect(
            method = "compile",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/RenderChunkRegion;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", ordinal = 1)
    )
    private BlockState redirectGetBlockStateAndCapture(RenderChunkRegion instance, BlockPos blockPos) {
        this.fluidloggedRenderChunkRegion = instance;
        this.fluidloggedBlockPos = blockPos;
        return instance.getBlockState(blockPos);
    }

    @Redirect(
            method = "compile",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFluidState()Lnet/minecraft/world/level/material/FluidState;")
    )
    private FluidState redirectGetFluidState(BlockState instance) {
        return this.fluidloggedRenderChunkRegion.getFluidState(this.fluidloggedBlockPos);
    }

}
