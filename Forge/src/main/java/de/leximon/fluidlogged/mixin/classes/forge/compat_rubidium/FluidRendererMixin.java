package de.leximon.fluidlogged.mixin.classes.forge.compat_rubidium;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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
