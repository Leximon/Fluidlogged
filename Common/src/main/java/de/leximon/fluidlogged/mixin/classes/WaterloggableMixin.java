package de.leximon.fluidlogged.mixin.classes;

import de.leximon.fluidlogged.Constants;
import de.leximon.fluidlogged.FluidloggedCommon;
import de.leximon.fluidlogged.core.FluidloggedConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Optional;

@Mixin(SimpleWaterloggedBlock.class)
public interface WaterloggableMixin {

    /**
     * @author Leximon (fluidlogged)
     * @reason to allow waterloggable blocks to be loggable with any fluid
     */
    @Overwrite
    default boolean canPlaceLiquid(BlockGetter world, BlockPos pos, BlockState state, Fluid fluid) {
        if(state.hasProperty(FluidloggedCommon.PROPERTY_FLUID))
            return state.getValue(FluidloggedCommon.PROPERTY_FLUID) == 0
                    && !state.getValue(BlockStateProperties.WATERLOGGED)
                    && (fluid.equals(Fluids.WATER) || FluidloggedConfig.fluidsLocked.contains(Registry.FLUID.getKey(fluid)));
        else return !state.getValue(BlockStateProperties.WATERLOGGED) && (fluid.equals(Fluids.WATER));
    }

    /**
     * @author Leximon (fluidlogged)
     * @reason to allow waterloggable blocks to be loggable with any fluid
     */
    @Overwrite
    default boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluidState) {
        Fluid fluid = fluidState.getType();
        if(state.hasProperty(FluidloggedCommon.PROPERTY_FLUID) && !state.getValue(BlockStateProperties.WATERLOGGED) && state.getValue(FluidloggedCommon.PROPERTY_FLUID) == 0) {
            if (!world.isClientSide()) {
                BlockState newState = state;
                if (fluid.equals(Fluids.WATER))
                    newState = newState.setValue(BlockStateProperties.WATERLOGGED, true);
                int index = FluidloggedCommon.getFluidIndex(fluid);
                if (index == -1) {
                    Constants.LOGGER.warn("Tried to fill a block with a not loggable fluid!");
                    return false;
                }
                world.setBlock(pos, newState.setValue(FluidloggedCommon.PROPERTY_FLUID, index), Block.UPDATE_ALL);
                world.scheduleTick(pos, fluid, fluid.getTickDelay(world));
            }
            return true;
        } else if(!state.getValue(BlockStateProperties.WATERLOGGED)) {
            if (!world.isClientSide()) {
                world.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, true), Block.UPDATE_ALL);
                world.scheduleTick(pos, fluid, fluid.getTickDelay(world));
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * @author Leximon (fluidlogged)
     * @reason to allow waterloggable blocks to be loggable with any fluid
     */
    @Overwrite
    default ItemStack pickupBlock(LevelAccessor world, BlockPos pos, BlockState state) {
        if(state.getValue(BlockStateProperties.WATERLOGGED) || (state.hasProperty(FluidloggedCommon.PROPERTY_FLUID) && state.getValue(FluidloggedCommon.PROPERTY_FLUID) > 0)) {
            Fluid fluid = FluidloggedCommon.getFluid(state);
            if(state.getValue(BlockStateProperties.WATERLOGGED))
                fluid = Fluids.WATER;
            if(state.hasProperty(FluidloggedCommon.PROPERTY_FLUID))
                state = state.setValue(FluidloggedCommon.PROPERTY_FLUID, 0);
            world.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, false), Block.UPDATE_ALL);
            if (!state.canSurvive(world, pos)) {
                world.destroyBlock(pos, true);
            }
            if (fluid == null)
                return ItemStack.EMPTY;
            return new ItemStack(fluid.getBucket());
        }
        return ItemStack.EMPTY;
    }

    /**
     * @author Leximon (fluidlogged)
     * @reason to allow waterloggable blocks to be loggable with any fluid
     */
    @Overwrite
    default Optional<SoundEvent> getPickupSound() {
        return Optional.empty();
    }

}
