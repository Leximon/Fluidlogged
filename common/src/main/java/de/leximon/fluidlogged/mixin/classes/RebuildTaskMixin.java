package de.leximon.fluidlogged.mixin.classes;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SectionCompiler.class)
public class RebuildTaskMixin {

    @Redirect(
            method = "compile*",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFluidState()Lnet/minecraft/world/level/material/FluidState;")
    )
    private FluidState redirectGetFluidState(
            BlockState instance,
            @Local(argsOnly = true) RenderChunkRegion region,
            @Local(ordinal = 2) BlockPos pos
    ) {
        return region.getFluidState(pos);
    }

}
