package de.leximon.fluidlogged.mixin;


import de.leximon.fluidlogged.core.FluidloggedConfig;
import de.leximon.fluidlogged.Fluidlogged;
import net.minecraft.core.BlockPos;
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
public interface SimpleWaterLoggedBlockMixin
{

    /**
     * @author Leximon (fluidlogged)
     * @reason to allow waterloggable blocks to be loggable with any fluid
     */
    @Overwrite
    default boolean canPlaceLiquid(BlockGetter world, BlockPos blockPos, BlockState blockState, Fluid fluid)
    {
        if(blockState.hasProperty(Fluidlogged.FLUIDLOGGED))
        {
            return (blockState.getValue(Fluidlogged.FLUIDLOGGED).equals("")
                    && !blockState.getValue(BlockStateProperties.WATERLOGGED)
                    && (fluid.equals(Fluids.WATER) || FluidloggedConfig.getFluidList().contains(fluid.getRegistryName().toString())));
        }


        else
            return !blockState.getValue(BlockStateProperties.WATERLOGGED) && (fluid.equals(Fluids.WATER));
    }

    /**
     * @author Leximon (fluidlogged)
     * @reason to allow waterloggable blocks to be loggable with any fluid
     */
    @Overwrite
    default boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluidState)
    {
        Fluid fluid = fluidState.getType();
        if(state.hasProperty(Fluidlogged.FLUIDLOGGED) && !state.getValue(BlockStateProperties.WATERLOGGED) && state.getValue(Fluidlogged.FLUIDLOGGED).equals(""))
        {
            if (!world.isClientSide())
            {
                BlockState newState = state;
                String value = "";

                if (fluid.equals(Fluids.WATER))
                {
                    newState = newState.setValue(BlockStateProperties.WATERLOGGED, true);
                }
                else
                {
                    value = Fluidlogged.getFluidString(fluid);
                    if (value.equals(""))
                    {
                        Fluidlogged.LOGGER.warn("Tried to fill a block with a not loggable fluid!");
                        return false;
                    }
                }
                world.setBlock(pos, newState.setValue(Fluidlogged.FLUIDLOGGED, value), Block.UPDATE_ALL);
                world.scheduleTick(pos, fluid, fluid.getTickDelay(world));
            }
            return true;
        }
        else if(!state.getValue(BlockStateProperties.WATERLOGGED))
        {
            if (!world.isClientSide())
            {
                world.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, true), Block.UPDATE_ALL);
                world.scheduleTick(pos, fluid, fluid.getTickDelay(world));
            }
            return true;
        }
        return false;

        /*Fluid fluid = fluidState.getType();
        if(state.hasProperty(Fluidlogged.PROPERTY_FLUID) && !state.getValue(BlockStateProperties.WATERLOGGED) && state.getValue(Fluidlogged.PROPERTY_FLUID) == 0) {

            if (!world.isClientSide()) {
                BlockState newState = state;
                if (fluid.equals(Fluids.WATER))
                    newState = newState.setValue(BlockStateProperties.WATERLOGGED, true);
                int index = Fluidlogged.getFluidIndex(fluid);
                if (index == -1) {
                    Fluidlogged.LOGGER.warn("Tried to fill a block with a not loggable fluid!");
                    return false;
                }
                world.setBlock(pos, newState.setValue(Fluidlogged.PROPERTY_FLUID, index), Block.UPDATE_ALL);
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
        }*/
    }
    /**
     * @author Leximon (fluidlogged)
     * @reason to allow waterloggable blocks to be loggable with any fluid
     */
    @Overwrite
    default ItemStack pickupBlock(LevelAccessor world, BlockPos pos, BlockState state) {
        if(state.getValue(BlockStateProperties.WATERLOGGED) || (state.hasProperty(Fluidlogged.FLUIDLOGGED) && !state.getValue(Fluidlogged.FLUIDLOGGED).equals("")))
        {
            Fluid fluid = Fluidlogged.getFluid(state);
            if(state.getValue(BlockStateProperties.WATERLOGGED))
            {
                fluid = Fluids.WATER;
            }
            if(state.hasProperty(Fluidlogged.FLUIDLOGGED))
            {
                state = state.setValue(Fluidlogged.FLUIDLOGGED, "");
            }
            world.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, false), Block.UPDATE_ALL);
            if (!state.canSurvive(world, pos))
            {
                world.destroyBlock(pos, true);
            }
            if (fluid == null)
                return ItemStack.EMPTY;
            return new ItemStack(fluid.getBucket());
        }
        return ItemStack.EMPTY;

        /*if(state.getValue(BlockStateProperties.WATERLOGGED) || (state.hasProperty(Fluidlogged.PROPERTY_FLUID) && state.getValue(Fluidlogged.PROPERTY_FLUID) > 0)) {
            Fluid fluid = Fluidlogged.getFluid(state);
            if(state.getValue(BlockStateProperties.WATERLOGGED))
                fluid = Fluids.WATER;
            if(state.hasProperty(Fluidlogged.PROPERTY_FLUID))
                state = state.setValue(Fluidlogged.PROPERTY_FLUID, 0);
            world.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, false), Block.UPDATE_ALL);
            if (!state.canSurvive(world, pos)) {
                world.destroyBlock(pos, true);
            }
            if (fluid == null)
                return ItemStack.EMPTY;
            return new ItemStack(fluid.getBucket());
        }
        return ItemStack.EMPTY;*/
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
