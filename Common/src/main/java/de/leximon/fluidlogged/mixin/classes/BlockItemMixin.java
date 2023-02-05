package de.leximon.fluidlogged.mixin.classes;

import de.leximon.fluidlogged.FluidloggedCommon;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {

    @ModifyVariable(method = "getPlacementState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BlockItem;canPlace(Lnet/minecraft/world/item/context/BlockPlaceContext;Lnet/minecraft/world/level/block/state/BlockState;)Z"), ordinal = 0)
    private BlockState injectCustomFluidPlacementState(BlockState placementState, BlockPlaceContext ctx) {
        if (placementState == null)
            return null;
        if (!placementState.hasProperty(FluidloggedCommon.PROPERTY_FLUID))
            return placementState;
        // check for slab because we have to remove the fluid if double slabbed
        if (placementState.getBlock() instanceof SlabBlock && placementState.getValue(SlabBlock.TYPE) == SlabType.DOUBLE)
            return placementState;
        FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
        int index = FluidloggedCommon.getFluidIndex(fluidState.getType());
        if (index != -1)
            return placementState.setValue(FluidloggedCommon.PROPERTY_FLUID, index);
        return placementState;
    }

    @Shadow public abstract Block getBlock();

}
