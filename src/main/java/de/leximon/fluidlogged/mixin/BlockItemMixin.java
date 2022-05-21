package de.leximon.fluidlogged.mixin;

import de.leximon.fluidlogged.Fluidlogged;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.world.item.context.BlockPlaceContext;

@Mixin(BlockItem.class)
public class BlockItemMixin
{
    @Redirect(method = "getPlacementState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getStateForPlacement(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState injectFluidPlacementState(Block instance, BlockPlaceContext ctx)
    {
        BlockState placementState = instance.getStateForPlacement(ctx);

        if(placementState == null)
            return null;

        if(!placementState.hasProperty(Fluidlogged.PROPERTY_FLUID))
            return placementState;

        FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
        int index = Fluidlogged.getFluidIndex(fluidState.getType());

        if(index != -1)
            return placementState.setValue(Fluidlogged.PROPERTY_FLUID, index);

        return placementState;
    }
}
