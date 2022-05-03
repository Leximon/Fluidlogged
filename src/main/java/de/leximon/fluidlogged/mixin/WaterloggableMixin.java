package de.leximon.fluidlogged.mixin;

import de.leximon.fluidlogged.FluidloggedMod;
import de.leximon.fluidlogged.core.FluidloggedConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
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
        return state.get(FluidloggedMod.PROPERTY_FLUID) == 0
                && !state.get(Properties.WATERLOGGED)
                && (fluid.equals(Fluids.WATER) || FluidloggedConfig.fluids.contains(Registry.FLUID.getId(fluid).toString()));
    }

    /**
     * @author Leximon (fluidlogged)
     * @reason to allow waterloggable blocks to be loggable with any fluid
     */
    @Overwrite
    default boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        Fluid fluid = fluidState.getFluid();
        if (!state.get(Properties.WATERLOGGED)
                && state.get(FluidloggedMod.PROPERTY_FLUID) == 0) {
            if (!world.isClient()) {
                BlockState newState = state;
                if (fluid.equals(Fluids.WATER))
                    newState = newState.with(Properties.WATERLOGGED, true);
                int index = FluidloggedMod.getFluidIndex(fluid);
                if (index == -1) {
                    FluidloggedMod.LOGGER.warn("Tried to fill a block with a not loggable fluid!");
                    return false;
                }
                world.setBlockState(pos, newState.with(FluidloggedMod.PROPERTY_FLUID, index), Block.NOTIFY_ALL);
                world.createAndScheduleFluidTick(pos, fluid, fluid.getTickRate(world));
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
        if(state.get(Properties.WATERLOGGED) || state.get(FluidloggedMod.PROPERTY_FLUID) > 0) {
            Fluid fluid = FluidloggedMod.getFluid(state);
            if(state.get(Properties.WATERLOGGED))
                fluid = Fluids.WATER;
            world.setBlockState(pos, state.with(Properties.WATERLOGGED, false).with(FluidloggedMod.PROPERTY_FLUID, 0), Block.NOTIFY_ALL);
            if (!state.canPlaceAt(world, pos)) {
                world.breakBlock(pos, true);
            }
            Item item = FluidloggedMod.fluidBuckets.get(fluid);
            if(item == null)
                return ItemStack.EMPTY;
            return new ItemStack(item);
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
