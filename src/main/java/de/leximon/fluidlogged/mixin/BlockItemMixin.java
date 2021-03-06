package de.leximon.fluidlogged.mixin;

import de.leximon.fluidlogged.FluidloggedMod;
import de.leximon.fluidlogged.core.FluidloggedConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockItem.class)
public class BlockItemMixin {

    @Redirect(method = "getPlacementState", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getPlacementState(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/block/BlockState;"))
    private BlockState injectFluidPlacementState(Block instance, ItemPlacementContext ctx) {
        BlockState placementState = instance.getPlacementState(ctx);
        if(placementState == null)
            return null;
        if(!placementState.contains(FluidloggedMod.PROPERTY_FLUID))
            return placementState;
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        int index = FluidloggedMod.getFluidIndex(fluidState.getFluid());
        if(index != -1)
            return placementState.with(FluidloggedMod.PROPERTY_FLUID, index);
        return placementState;
    }

}
