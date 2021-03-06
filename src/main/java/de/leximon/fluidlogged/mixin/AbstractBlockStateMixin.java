package de.leximon.fluidlogged.mixin;

import de.leximon.fluidlogged.FluidloggedMod;
import de.leximon.fluidlogged.mixin.accessor.AbstractBlockAccessor;
import de.leximon.fluidlogged.mixin.accessor.AbstractBlockSettingsAccessor;
import de.leximon.fluidlogged.mixin.accessor.StateAccessor;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.ToIntFunction;

@Mixin(value = AbstractBlock.AbstractBlockState.class, priority = 1010)
public abstract class AbstractBlockStateMixin {

    @Shadow protected abstract BlockState asBlockState();

    @Shadow public abstract Block getBlock();

    // luminance for example lavalogged blocks
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/function/ToIntFunction;applyAsInt(Ljava/lang/Object;)I"))
    private <T> int injectLuminance(ToIntFunction<T> instance, T t) {
        if(t instanceof BlockState state
                && ((StateAccessor) state).fluidlogged_getEntries() != null
                && state.contains(FluidloggedMod.PROPERTY_FLUID)) {
            Fluid fluid = FluidloggedMod.getFluid(state);
            FluidBlock block = FluidloggedMod.fluidBlocks.get(fluid);
            if(block != null) {
                AbstractBlock.Settings settings = ((AbstractBlockAccessor) block).fluidlogged_getSettings();
                return ((AbstractBlockSettingsAccessor) settings).fluidlogged_getLuminance().applyAsInt((BlockState) t);
            }
        }
        return instance.applyAsInt(t);
    }

    /**
     * @author Leximon (fluidlogged)
     * @reason to allow waterloggable blocks to be loggable with any fluid
     */
    @Overwrite
    public FluidState getFluidState() {
        Block block = this.getBlock();
        BlockState state = this.asBlockState();
        if (state.contains(Properties.WATERLOGGED) && state.get(Properties.WATERLOGGED))
            return block.getFluidState(state);
        if (state.contains(FluidloggedMod.PROPERTY_FLUID)) {
            Fluid f = FluidloggedMod.getFluid(state);
            if (f != null)
                return f.getDefaultState();
        }
        return block.getFluidState(state);
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
