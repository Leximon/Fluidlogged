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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {

    @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
    private void fl_injectFluidPlacementState(ItemPlacementContext ctx, CallbackInfoReturnable<BlockState> cir) {
        BlockState placementState = this.getBlock().getPlacementState(ctx);
        if (placementState == null) {
            cir.setReturnValue(null);
            return;
        }
        if (!placementState.contains(FluidloggedMod.PROPERTY_FLUID)) {
            cir.setReturnValue(placementState);
            return;
        }
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        int index = FluidloggedMod.getFluidIndex(fluidState.getFluid());
        if(index != -1) {
            cir.setReturnValue(placementState.with(FluidloggedMod.PROPERTY_FLUID, index));
            return;
        }
        cir.setReturnValue(placementState);
    }

    @Shadow public abstract Block getBlock();

}
