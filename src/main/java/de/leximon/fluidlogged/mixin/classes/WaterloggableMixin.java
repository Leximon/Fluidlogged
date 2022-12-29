package de.leximon.fluidlogged.mixin.classes;

import de.leximon.fluidlogged.Fluidlogged;
import de.leximon.fluidlogged.core.FluidloggedConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Optional;

@Mixin(Waterloggable.class)
public interface WaterloggableMixin {

    /**
     * @author Leximon (fluidlogged)
     * @reason to allow waterloggable blocks to be loggable with any fluid
     */
    @Overwrite
    default boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        if(state.contains(Fluidlogged.PROPERTY_FLUID))
            return state.get(Fluidlogged.PROPERTY_FLUID) == 0
                    && !state.get(Properties.WATERLOGGED)
                    && (fluid.equals(Fluids.WATER) || FluidloggedConfig.fluidsLocked.contains(Registries.FLUID.getId(fluid)));
        else return !state.get(Properties.WATERLOGGED) && (fluid.equals(Fluids.WATER));
    }

    /**
     * @author Leximon (fluidlogged)
     * @reason to allow waterloggable blocks to be loggable with any fluid
     */
    @Overwrite
    default boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        Fluid fluid = fluidState.getFluid();
        if(state.contains(Fluidlogged.PROPERTY_FLUID) && !state.get(Properties.WATERLOGGED) && state.get(Fluidlogged.PROPERTY_FLUID) == 0) {
            if (!world.isClient()) {
                BlockState newState = state;
                if (fluid.equals(Fluids.WATER))
                    newState = newState.with(Properties.WATERLOGGED, true);
                int index = Fluidlogged.getFluidIndex(fluid);
                if (index == -1) {
                    Fluidlogged.LOGGER.warn("Tried to fill a block with a not loggable fluid!");
                    return false;
                }
                world.setBlockState(pos, newState.with(Fluidlogged.PROPERTY_FLUID, index), Block.NOTIFY_ALL);
                world.scheduleFluidTick(pos, fluid, fluid.getTickRate(world));
            }
            return true;
        } else if(!state.get(Properties.WATERLOGGED)) {
            if (!world.isClient()) {
                world.setBlockState(pos, state.with(Properties.WATERLOGGED, true), Block.NOTIFY_ALL);
                world.scheduleFluidTick(pos, fluid, fluid.getTickRate(world));
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
    default ItemStack tryDrainFluid(WorldAccess world, BlockPos pos, BlockState state) {
        if(state.get(Properties.WATERLOGGED) || (state.contains(Fluidlogged.PROPERTY_FLUID) && state.get(Fluidlogged.PROPERTY_FLUID) > 0)) {
            Fluid fluid = Fluidlogged.getFluid(state);
            if(state.get(Properties.WATERLOGGED))
                fluid = Fluids.WATER;
            if(state.contains(Fluidlogged.PROPERTY_FLUID))
                state = state.with(Fluidlogged.PROPERTY_FLUID, 0);
            world.setBlockState(pos, state.with(Properties.WATERLOGGED, false), Block.NOTIFY_ALL);
            if (!state.canPlaceAt(world, pos)) {
                world.breakBlock(pos, true);
            }
            if (fluid == null)
                return ItemStack.EMPTY;
            return new ItemStack(fluid.getBucketItem());
        }
        return ItemStack.EMPTY;
    }

    /**
     * @author Leximon (fluidlogged)
     * @reason to allow waterloggable blocks to be loggable with any fluid
     */
    @Overwrite
    default Optional<SoundEvent> getBucketFillSound() {
        return Optional.empty();
    }

}
