package de.leximon.fluidlogged.mixin.classes.rendering;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(LiquidBlockRenderer.class)
public class LiquidBlockRendererMixin {

    @Redirect(
            method = "tesselate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFluidState()Lnet/minecraft/world/level/material/FluidState;", ordinal = 0)
    )
    private FluidState redirectGetFluidStateDown(BlockState blockState, BlockAndTintGetter blockAndTintGetter, BlockPos blockPos, VertexConsumer vertexConsumer, BlockState blockState2, FluidState fluidState) {
        return blockAndTintGetter.getFluidState(blockPos.relative(Direction.DOWN));
    }

    @Redirect(
            method = "tesselate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFluidState()Lnet/minecraft/world/level/material/FluidState;", ordinal = 1)
    )
    private FluidState redirectGetFluidStateUp(BlockState blockState, BlockAndTintGetter blockAndTintGetter, BlockPos blockPos, VertexConsumer vertexConsumer, BlockState blockState2, FluidState fluidState) {
        return blockAndTintGetter.getFluidState(blockPos.relative(Direction.UP));
    }

    @Redirect(
            method = "tesselate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFluidState()Lnet/minecraft/world/level/material/FluidState;", ordinal = 2)
    )
    private FluidState redirectGetFluidStateNorth(BlockState blockState, BlockAndTintGetter blockAndTintGetter, BlockPos blockPos, VertexConsumer vertexConsumer, BlockState blockState2, FluidState fluidState) {
        return blockAndTintGetter.getFluidState(blockPos.relative(Direction.NORTH));
    }

    @Redirect(
            method = "tesselate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFluidState()Lnet/minecraft/world/level/material/FluidState;", ordinal = 3)
    )
    private FluidState redirectGetFluidStateSouth(BlockState blockState, BlockAndTintGetter blockAndTintGetter, BlockPos blockPos, VertexConsumer vertexConsumer, BlockState blockState2, FluidState fluidState) {
        return blockAndTintGetter.getFluidState(blockPos.relative(Direction.SOUTH));
    }

    @Redirect(
            method = "tesselate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFluidState()Lnet/minecraft/world/level/material/FluidState;", ordinal = 4)
    )
    private FluidState redirectGetFluidStateWest(BlockState blockState, BlockAndTintGetter blockAndTintGetter, BlockPos blockPos, VertexConsumer vertexConsumer, BlockState blockState2, FluidState fluidState) {
        return blockAndTintGetter.getFluidState(blockPos.relative(Direction.WEST));
    }

    @Redirect(
            method = "tesselate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFluidState()Lnet/minecraft/world/level/material/FluidState;", ordinal = 5)
    )
    private FluidState redirectGetFluidStateEast(BlockState blockState, BlockAndTintGetter blockAndTintGetter, BlockPos blockPos, VertexConsumer vertexConsumer, BlockState blockState2, FluidState fluidState) {
        return blockAndTintGetter.getFluidState(blockPos.relative(Direction.EAST));
    }


    @Redirect(
            method = "getHeight(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/material/Fluid;Lnet/minecraft/core/BlockPos;)F",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFluidState()Lnet/minecraft/world/level/material/FluidState;")
    )
    private FluidState redirectGetFluidState2(BlockState instance, BlockAndTintGetter blockAndTintGetter, Fluid fluid, BlockPos blockPos) {
        return blockAndTintGetter.getFluidState(blockPos);
    }



    @Redirect(
            method = "getHeight(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/material/Fluid;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)F",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/BlockAndTintGetter;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
    )
    private BlockState disableGetBlockState3(BlockAndTintGetter instance, BlockPos blockPos) {
        // do nothing
        return null;
    }

    @Redirect(
            method = "getHeight(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/material/Fluid;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)F",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFluidState()Lnet/minecraft/world/level/material/FluidState;")
    )
    private FluidState redirectGetFluidState3(BlockState instance, BlockAndTintGetter blockAndTintGetter, Fluid fluid, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
        return blockAndTintGetter.getFluidState(blockPos.above());
    }

}
