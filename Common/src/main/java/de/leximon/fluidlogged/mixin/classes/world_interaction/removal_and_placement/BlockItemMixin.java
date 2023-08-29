package de.leximon.fluidlogged.mixin.classes.world_interaction.removal_and_placement;

import de.leximon.fluidlogged.Fluidlogged;
import de.leximon.fluidlogged.mixin.extensions.LevelExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {

    @Shadow protected abstract boolean placeBlock(BlockPlaceContext blockPlaceContext, BlockState blockState);

    @Redirect(
            method = "place",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BlockItem;placeBlock(Lnet/minecraft/world/item/context/BlockPlaceContext;Lnet/minecraft/world/level/block/state/BlockState;)Z")
    )
    private boolean redirectFluidPlacement(BlockItem instance, BlockPlaceContext context, BlockState blockState) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        FluidState fluidState = level.getFluidState(pos);

        if (!placeBlock(context, blockState))
            return false;

        if (fluidState.isEmpty() || (blockState.hasProperty(BlockStateProperties.WATERLOGGED) && blockState.getValue(BlockStateProperties.WATERLOGGED)))
            return true;

        if (!Fluidlogged.canPlaceFluid(level, pos, blockState, fluidState.getType())) {
            ((LevelExtension) level).setFluid(pos, Fluids.EMPTY.defaultFluidState(), Block.UPDATE_ALL | Fluidlogged.UPDATE_SCHEDULE_FLUID_TICK);
            return true;
        }

        ((LevelExtension) level).setFluid(pos, fluidState, Block.UPDATE_ALL | Fluidlogged.UPDATE_SCHEDULE_FLUID_TICK);
        return true;
    }


}
