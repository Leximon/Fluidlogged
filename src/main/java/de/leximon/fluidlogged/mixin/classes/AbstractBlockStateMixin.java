package de.leximon.fluidlogged.mixin.classes;

import de.leximon.fluidlogged.Fluidlogged;
import de.leximon.fluidlogged.mixin.classes.accessor.AbstractBlockAccessor;
import de.leximon.fluidlogged.mixin.classes.accessor.AbstractBlockSettingsAccessor;
import de.leximon.fluidlogged.mixin.classes.accessor.StateAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.ToIntFunction;
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

@SuppressWarnings("deprecation")
@Mixin(value = BlockBehaviour.BlockStateBase.class, priority = 1010)
public abstract class AbstractBlockStateMixin {

    // luminance for example lavalogged blocks
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/function/ToIntFunction;applyAsInt(Ljava/lang/Object;)I"))
    private <T> int injectLuminance(ToIntFunction<T> instance, T t) {
        if(t instanceof BlockState state
                && ((StateAccessor) state).fl_getEntries() != null
                && state.hasProperty(Fluidlogged.PROPERTY_FLUID)) {
            Fluid fluid = Fluidlogged.getFluid(state);
            LiquidBlock block = Fluidlogged.fluidBlocks.get(fluid);
            if(block != null) {
                BlockBehaviour.Properties settings = ((AbstractBlockAccessor) block).fl_getSettings();
                return ((AbstractBlockSettingsAccessor) settings).fl_getLuminance().applyAsInt((BlockState) t);
            }
        }
        return instance.applyAsInt(t);
    }

    @Redirect(method = "updateShape", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;updateShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState makeCustomFluidTickable(Block instance, BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        Fluid f = Fluidlogged.getFluid(state);
        if (!(f == null || Fluids.EMPTY.equals(f)))
            world.scheduleTick(pos, f, f.getTickDelay(world));
        return instance.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Redirect(method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getCollisionShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
    private VoxelShape injectCustomFluidCollsionShape(Block instance, BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return instance.getCollisionShape(
                state.hasProperty(Fluidlogged.PROPERTY_FLUID)
                        ? state.setValue(Fluidlogged.PROPERTY_FLUID, 0)
                        : state,
                world, pos, context
        );
    }

    @Redirect(method = "getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
    private VoxelShape injectCustomFluidOutlineShape(Block instance, BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return instance.getShape(
                state.hasProperty(Fluidlogged.PROPERTY_FLUID)
                        ? state.setValue(Fluidlogged.PROPERTY_FLUID, 0)
                        : state,
                world, pos, context
        );
    }

    @Redirect(method = "getBlockSupportShape", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getBlockSupportShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
    private VoxelShape injectCustomFluidSidesShape(Block instance, BlockState state, BlockGetter world, BlockPos pos) {
        CollisionContext context = CollisionContext.empty();
        return instance.getCollisionShape(
                state.hasProperty(Fluidlogged.PROPERTY_FLUID)
                        ? state.setValue(Fluidlogged.PROPERTY_FLUID, 0)
                        : state,
                world, pos, context
        );
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
        if (state.hasProperty(Fluidlogged.PROPERTY_FLUID)) {
            Fluid f = Fluidlogged.getFluid(state);
            if (f != null)
                return f.defaultFluidState();
        }
        return block.getFluidState(state);
    }


    @Shadow protected abstract BlockState asState();
    @Shadow public abstract Block getBlock();

}
