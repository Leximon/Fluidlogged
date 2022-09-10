package de.leximon.fluidlogged.mixin;

import de.leximon.fluidlogged.FluidloggedMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
//
//    @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
//    private void injectCustomFluidPlacementState(ItemPlacementContext ctx, CallbackInfoReturnable<BlockState> cir) {
//        BlockState placementState = cir.getReturnValue();
//        if (placementState == null)
//            return;
//        if (!placementState.contains(FluidloggedMod.PROPERTY_FLUID)) {
//            cir.setReturnValue(placementState);
//            return;
//        }
//        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
//        int index = FluidloggedMod.getFluidIndex(fluidState.getFluid());
//        if(index != -1)
//            cir.setReturnValue(placementState.with(FluidloggedMod.PROPERTY_FLUID, index));
//    }

    @ModifyVariable(method = "getPlacementState", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BlockItem;canPlace(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z"), ordinal = 0)
    private BlockState injectCustomFluidPlacementState(BlockState placementState, ItemPlacementContext ctx) {
        if (placementState == null)
            return null;
        if (!placementState.contains(FluidloggedMod.PROPERTY_FLUID))
            return placementState;
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        int index = FluidloggedMod.getFluidIndex(fluidState.getFluid());
        if (index != -1)
            return placementState.with(FluidloggedMod.PROPERTY_FLUID, index);
        return placementState;
    }

    @Shadow public abstract Block getBlock();

}
