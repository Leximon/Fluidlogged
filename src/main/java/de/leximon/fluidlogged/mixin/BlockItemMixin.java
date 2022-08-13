package de.leximon.fluidlogged.mixin;

import de.leximon.fluidlogged.FluidloggedMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {

    @Shadow public abstract Block getBlock();

    @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
    private void fl_injectFluidPlacementState(BlockPlaceContext ctx, CallbackInfoReturnable<BlockState> cir) {
        BlockState placementState = this.getBlock().getStateForPlacement(ctx);
        if(placementState == null) {
            cir.setReturnValue(null);
            return;
        }
        if(!placementState.hasProperty(FluidloggedMod.PROPERTY_FLUID)) {
            cir.setReturnValue(placementState);
            return;
        }
        FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
        int index = FluidloggedMod.getFluidIndex(fluidState.getType());
        if(index != -1) {
            cir.setReturnValue(placementState.setValue(FluidloggedMod.PROPERTY_FLUID, index));
            return;
        }
        cir.setReturnValue(placementState);
    }

//    @Redirect(method = "getPlacementState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getStateForPlacement(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/level/block/state/BlockState;"))
//    private BlockState fl_injectFluidPlacementState(Block instance, BlockPlaceContext ctx) {
//        BlockState placementState = instance.getStateForPlacement(ctx);
//        if(placementState == null)
//            return null;
//        if(!placementState.hasProperty(FluidloggedMod.PROPERTY_FLUID))
//            return placementState;
//        FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
//        int index = FluidloggedMod.getFluidIndex(fluidState.getType());
//        if(index != -1)
//            return placementState.setValue(FluidloggedMod.PROPERTY_FLUID, index);
//        return placementState;
//    }

}
