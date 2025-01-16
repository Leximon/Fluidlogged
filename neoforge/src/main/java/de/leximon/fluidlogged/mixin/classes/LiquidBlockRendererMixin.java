package de.leximon.fluidlogged.mixin.classes;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LiquidBlockRenderer.class)
public abstract class LiquidBlockRendererMixin {

    @Shadow
    public static boolean shouldRenderFace(BlockAndTintGetter level, BlockPos pos, FluidState fluidState, BlockState blockState, Direction side, BlockState otherState) {
        return false;
    }

    @ModifyVariable(
            method = "tesselate",
            at = @At("STORE"),
            ordinal = 1
    )
    private boolean modifyIsNeighborSameFluid(
            boolean original,
            @Local(argsOnly = true) BlockAndTintGetter level,
            @Local(argsOnly = true) BlockPos pos
    ) {
        if (!original) {
            return level.getFluidState(pos.relative(Direction.UP)).getType()
                    .isSame(level.getFluidState(pos).getType());
        }
        return true;
    }

    @Redirect(
            method = "tesselate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/LiquidBlockRenderer;shouldRenderFace(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/FluidState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/block/state/BlockState;)Z")
    )
    private boolean redirectShouldRenderFace(
            BlockAndTintGetter level, BlockPos pos, FluidState fluidState, BlockState blockState, Direction side, BlockState otherState
    ) {
        boolean original = shouldRenderFace(level, pos, fluidState, blockState, side, otherState);
        if (original) {
            return !level.getFluidState(pos.relative(side)).getType()
                    .isSame(level.getFluidState(pos).getType());
        }
        return false;
    }

    @Redirect(
            method = "tesselate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFluidState()Lnet/minecraft/world/level/material/FluidState;", ordinal = 0)
    )
    private FluidState redirectGetFluidStateDown(BlockState blockState, BlockAndTintGetter level, BlockPos blockPos, VertexConsumer vertexConsumer, BlockState blockState2, FluidState fluidState) {
        return level.getFluidState(blockPos.relative(Direction.DOWN));
    }

    @Redirect(
            method = "tesselate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFluidState()Lnet/minecraft/world/level/material/FluidState;", ordinal = 1)
    )
    private FluidState redirectGetFluidStateUp(BlockState blockState, BlockAndTintGetter level, BlockPos blockPos, VertexConsumer vertexConsumer, BlockState blockState2, FluidState fluidState) {
        return level.getFluidState(blockPos.relative(Direction.UP));
    }

    @Redirect(
            method = "tesselate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFluidState()Lnet/minecraft/world/level/material/FluidState;", ordinal = 2)
    )
    private FluidState redirectGetFluidStateNorth(BlockState blockState, BlockAndTintGetter level, BlockPos blockPos, VertexConsumer vertexConsumer, BlockState blockState2, FluidState fluidState) {
        return level.getFluidState(blockPos.relative(Direction.NORTH));
    }

    @Redirect(
            method = "tesselate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFluidState()Lnet/minecraft/world/level/material/FluidState;", ordinal = 3)
    )
    private FluidState redirectGetFluidStateSouth(BlockState blockState, BlockAndTintGetter level, BlockPos blockPos, VertexConsumer vertexConsumer, BlockState blockState2, FluidState fluidState) {
        return level.getFluidState(blockPos.relative(Direction.SOUTH));
    }

    @Redirect(
            method = "tesselate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFluidState()Lnet/minecraft/world/level/material/FluidState;", ordinal = 4)
    )
    private FluidState redirectGetFluidStateWest(BlockState blockState, BlockAndTintGetter level, BlockPos blockPos, VertexConsumer vertexConsumer, BlockState blockState2, FluidState fluidState) {
        return level.getFluidState(blockPos.relative(Direction.WEST));
    }

    @Redirect(
            method = "tesselate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFluidState()Lnet/minecraft/world/level/material/FluidState;", ordinal = 5)
    )
    private FluidState redirectGetFluidStateEast(BlockState blockState, BlockAndTintGetter level, BlockPos blockPos, VertexConsumer vertexConsumer, BlockState blockState2, FluidState fluidState) {
        return level.getFluidState(blockPos.relative(Direction.EAST));
    }


    @Redirect(
            method = "getHeight(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/material/Fluid;Lnet/minecraft/core/BlockPos;)F",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFluidState()Lnet/minecraft/world/level/material/FluidState;")
    )
    private FluidState redirectGetFluidState2(BlockState instance, BlockAndTintGetter level, Fluid fluid, BlockPos blockPos) {
        return level.getFluidState(blockPos);
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
    private FluidState redirectGetFluidState3(BlockState instance, BlockAndTintGetter level, Fluid fluid, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
        return level.getFluidState(blockPos.above());
    }

}
