package de.leximon.fluidlogged.mixin.classes.sodium_compat;

import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "me/jellysquid/mods/sodium/client/render/chunk/compile/pipeline/FluidRenderer")
public class FluidRendererMixin {

    @Redirect(
            method = "fluidHeight",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getFluidState()Lnet/minecraft/world/level/material/FluidState;"
            )
    )
    private FluidState redirectFluidHeight(BlockState blockState, BlockAndTintGetter world, Fluid fluid, BlockPos blockPos, Direction direction) {
        return world.getFluidState(blockPos);
    }

}
