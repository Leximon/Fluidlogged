package de.leximon.fluidlogged.mixin;

import de.leximon.fluidlogged.Fluidlogged;
import de.leximon.fluidlogged.mixin.accessor.AbstractBlockAccessor;
import de.leximon.fluidlogged.mixin.accessor.AbstractBlockSettingsAccessor;
import de.leximon.fluidlogged.mixin.accessor.StateAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.ToIntFunction;

@Mixin(value = BlockBehaviour.BlockStateBase.class, priority = 1010)
public abstract class AbstractBlockStateMixin
{
    @Shadow
    protected abstract BlockState asState();

    @Shadow public abstract Block getBlock();

    // luminance for example lavalogged blocks
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/function/ToIntFunction;applyAsInt(Ljava/lang/Object;)I"))
    private <T> int injectLuminance(ToIntFunction<T> instance, T t) {
        if(t instanceof BlockState state
                && ((StateAccessor) state).fluidlogged_getEntries() != null
                && state.hasProperty(/*FluidLoggedProperties.FLUIDLOGGED*/ Fluidlogged.PROPERTY_FLUID)) {
            Fluid fluid = Fluidlogged.getFluid(state);
            LiquidBlock block = Fluidlogged.fluidBlocks.get(fluid);
            if(block != null) {
                BlockBehaviour.Properties settings = ((AbstractBlockAccessor) block).fluidlogged_getProperties();
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
        BlockState state = this.asState();
        if (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED))
            return block.getFluidState(state);
        if (state.hasProperty(/*FluidLoggedProperties.FLUIDLOGGED*/ Fluidlogged.PROPERTY_FLUID)) {
            Fluid f = Fluidlogged.getFluid(state);
            if (f != null)
                return f.defaultFluidState();
        }
        return block.getFluidState(state);
    }


    @Redirect(method = "updateShape", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/level/block/Block;updateShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState injectFlowingFluid(Block instance, BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        Fluid f = Fluidlogged.getFluid(state);
        if (!(f == null || Fluids.EMPTY.equals(f)))
            world.scheduleTick(pos, f, f.getTickDelay(world));
        return instance.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Redirect(method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getCollisionShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
    private VoxelShape injectCollisionShape(Block instance, BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return instance.getCollisionShape(
                state.hasProperty(/*FluidLoggedProperties.FLUIDLOGGED*/ Fluidlogged.PROPERTY_FLUID)
                        ? state.setValue(/*FluidLoggedProperties.FLUIDLOGGED*/ Fluidlogged.PROPERTY_FLUID, 0)
                        : state,
                world, pos, context
        );
    }

    // Highly likely to be the wrong shape function
    @Redirect(method = "getShape(" + "Lnet/minecraft/world/level/BlockGetter;" + "Lnet/minecraft/core/BlockPos;" + "Lnet/minecraft/world/phys/shapes/CollisionContext;)" + "Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;" + "getShape(" + "Lnet/minecraft/world/level/block/state/BlockState;" + "Lnet/minecraft/world/level/BlockGetter;" + "Lnet/minecraft/core/BlockPos;" + "Lnet/minecraft/world/phys/shapes/CollisionContext;)" + "Lnet/minecraft/world/phys/shapes/VoxelShape;"))
    private VoxelShape injectOutlineShape(Block instance, BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return instance.getShape(
                state.hasProperty(/*FluidLoggedProperties.FLUIDLOGGED*/ Fluidlogged.PROPERTY_FLUID)
                        ? state.setValue(/*FluidLoggedProperties.FLUIDLOGGED*/ Fluidlogged.PROPERTY_FLUID, 0)
                        : state,
                world, pos, context
        );
    }

    @Redirect(method = "getBlockSupportShape", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getBlockSupportShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
    private VoxelShape injected(Block instance, BlockState state, BlockGetter world, BlockPos pos) {
        CollisionContext context = CollisionContext.empty();
        return instance.getCollisionShape(
                state.hasProperty(/*FluidLoggedProperties.FLUIDLOGGED*/ Fluidlogged.PROPERTY_FLUID)
                        ? state.setValue(/*FluidLoggedProperties.FLUIDLOGGED*/ Fluidlogged.PROPERTY_FLUID, 0)
                        : state,
                world, pos, context
        );
    }
}
