package de.leximon.fluidlogged.mixin;

import de.leximon.fluidlogged.FluidloggedMod;
import de.leximon.fluidlogged.mixin.accessor.BlockBehaviourPropertiesAccessor;
import de.leximon.fluidlogged.mixin.accessor.BlockBehaviourAccessor;
import de.leximon.fluidlogged.mixin.accessor.StateHolderAccessor;
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
public abstract class BlockStateBaseMixin {

    // luminance for example lavalogged blocks
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/function/ToIntFunction;applyAsInt(Ljava/lang/Object;)I"))
    private <T> int injectLuminance(ToIntFunction<T> instance, T t) {
        if(t instanceof BlockState state
                && ((StateHolderAccessor) state).fl_getValues() != null
                && state.hasProperty(FluidloggedMod.PROPERTY_FLUID)) {
            Fluid fluid = FluidloggedMod.getFluid(state);
            LiquidBlock block = FluidloggedMod.fluidBlocks.get(fluid);
            if(block != null) {
                BlockBehaviour.Properties settings = ((BlockBehaviourAccessor) block).fl_getProperties();
                return ((BlockBehaviourPropertiesAccessor) settings).fl_getLightEmission().applyAsInt((BlockState) t);
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
        if (state.hasProperty(FluidloggedMod.PROPERTY_FLUID)) {
            Fluid f = FluidloggedMod.getFluid(state);
            if (f != null)
                return f.defaultFluidState();
        }
        return block.getFluidState(state);
    }


    @Redirect(method = "updateShape", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;updateShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState injectFlowingFluid(Block instance, BlockState blockState, Direction direction, BlockState neighborBlockState, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos neighborBlockPos) {
        Fluid f = FluidloggedMod.getFluid(blockState);
        if (!(f == null || Fluids.EMPTY.equals(f)))
            levelAccessor.scheduleTick(blockPos, f, f.getTickDelay(levelAccessor));
        return instance.updateShape(blockState, direction, neighborBlockState, levelAccessor, blockPos, neighborBlockPos);
    }

    @Redirect(method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getCollisionShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
    private VoxelShape injectCollisionShape(Block instance, BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return instance.getCollisionShape(
                blockState.hasProperty(FluidloggedMod.PROPERTY_FLUID)
                        ? blockState.setValue(FluidloggedMod.PROPERTY_FLUID, 0)
                        : blockState,
                blockGetter, blockPos, collisionContext
        );
    }

    @Redirect(method = "getVisualShape", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getVisualShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
    private VoxelShape injectOutlineShape(Block instance, BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return instance.getShape(
                blockState.hasProperty(FluidloggedMod.PROPERTY_FLUID)
                        ? blockState.setValue(FluidloggedMod.PROPERTY_FLUID, 0)
                        : blockState,
                blockGetter, blockPos, collisionContext
        );
    }

    @Redirect(method = "getInteractionShape", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getInteractionShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
    private VoxelShape injected(Block instance, BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        CollisionContext context = CollisionContext.empty();
        return instance.getCollisionShape(
                blockState.hasProperty(FluidloggedMod.PROPERTY_FLUID)
                        ? blockState.setValue(FluidloggedMod.PROPERTY_FLUID, 0)
                        : blockState,
                blockGetter, blockPos, context
        );
    }

    @Shadow public abstract Block getBlock();
    @Shadow protected abstract BlockState asState();

}
