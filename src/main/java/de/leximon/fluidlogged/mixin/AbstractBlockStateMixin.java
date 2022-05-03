package de.leximon.fluidlogged.mixin;

import de.leximon.fluidlogged.FluidloggedMod;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockStateMixin {

    @Redirect(method = "getFluidState", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getFluidState(Lnet/minecraft/block/BlockState;)Lnet/minecraft/fluid/FluidState;"))
    private FluidState injectFluidState(Block instance, BlockState state) {
        if (state.contains(FluidloggedMod.PROPERTY_FLUID)) {
            Fluid f = FluidloggedMod.getFluid(state);
            if(f != null)
                return f.getDefaultState();
        }
        return instance.getFluidState(state);
    }

    @Redirect(method = "getStateForNeighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getStateForNeighborUpdate(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
    private BlockState injectFlowingFluid(Block instance, BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        Fluid f = FluidloggedMod.getFluid(state);
        if (!(f == null || Fluids.EMPTY.equals(f)))
            world.createAndScheduleFluidTick(pos, f, f.getTickRate(world));
        return instance.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Redirect(method = "getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getCollisionShape(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;"))
    private VoxelShape injectCollisionShape(Block instance, BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return instance.getCollisionShape(
                state.contains(FluidloggedMod.PROPERTY_FLUID)
                        ? state.with(FluidloggedMod.PROPERTY_FLUID, 0)
                        : state,
                world, pos, context
        );
    }

    @Redirect(method = "getOutlineShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getOutlineShape(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;"))
    private VoxelShape injectOutlineShape(Block instance, BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return instance.getOutlineShape(
                state.contains(FluidloggedMod.PROPERTY_FLUID)
                        ? state.with(FluidloggedMod.PROPERTY_FLUID, 0)
                        : state,
                world, pos, context
        );
    }

    @Redirect(method = "getSidesShape", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getSidesShape(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/shape/VoxelShape;"))
    private VoxelShape injected(Block instance, BlockState state, BlockView world, BlockPos pos) {
        ShapeContext context = ShapeContext.absent();
        return instance.getCollisionShape(
                state.contains(FluidloggedMod.PROPERTY_FLUID)
                        ? state.with(FluidloggedMod.PROPERTY_FLUID, 0)
                        : state,
                world, pos, context
        );
    }

}
