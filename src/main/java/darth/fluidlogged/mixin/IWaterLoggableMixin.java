package darth.fluidlogged.mixin;


import darth.fluidlogged.Fluidlogged;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Optional;

@Mixin(SimpleWaterloggedBlock.class)
public interface IWaterLoggableMixin
{
    /**
     * @author
     */
    @Overwrite
    default boolean canPlaceLiquid(BlockGetter world, BlockPos blockPos, BlockState blockState, Fluid fluid)
    {
        return !blockState.getValue(BlockStateProperties.WATERLOGGED) && fluid.isSource(blockState.getFluidState());
    }

    /**
     * @author
     */
    @Overwrite
    default boolean placeLiquid(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, FluidState fluidState)
    {
        Fluidlogged.LOGGER.info("TEST " + fluidState.getType().getRegistryName());
        if (!blockState.getValue(BlockStateProperties.WATERLOGGED))
        {
            if (!levelAccessor.isClientSide())
            {
                levelAccessor.setBlock(blockPos, blockState.setValue(BlockStateProperties.WATERLOGGED, Boolean.TRUE).setValue(darth.fluidlogged.BlockStateProperties.FLUIDLOGGED, fluidState.getType().getRegistryName().toString()), 3);
                levelAccessor.scheduleTick(blockPos, fluidState.getType(), fluidState.getType().getTickDelay(levelAccessor));
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * @author
     */
    @Overwrite
    default ItemStack pickupBlock(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
        if (blockState.getValue(BlockStateProperties.WATERLOGGED)) {
            levelAccessor.setBlock(blockPos, blockState.setValue(BlockStateProperties.WATERLOGGED, Boolean.FALSE).setValue(darth.fluidlogged.BlockStateProperties.FLUIDLOGGED, "minecraft:air"), Block.UPDATE_ALL);
            if (!blockState.canSurvive(levelAccessor, blockPos)) {
                levelAccessor.destroyBlock(blockPos, true);
            }
            Item result = ForgeRegistries.FLUIDS.getValue(ResourceLocation.tryParse(blockState.getValue(darth.fluidlogged.BlockStateProperties.FLUIDLOGGED))).getBucket();
            return new ItemStack(result);
        } else {
            return ItemStack.EMPTY;
        }
    }

    /**
     * @author
     */
    @Overwrite
    default Optional<SoundEvent> getPickupSound() {
        return Fluids.EMPTY.getPickupSound();
    }
}
