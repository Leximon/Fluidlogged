package de.leximon.fluidlogged.mixin.classes;

import de.leximon.fluidlogged.Fluidlogged;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SlabBlock.class)
public class SlabBlockMixin {

    @Inject(method = "getStateForPlacement", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    private void injectRemoveFluidloggedStateIfDouble(BlockPlaceContext ctx, CallbackInfoReturnable<BlockState> cir) {
        cir.setReturnValue(cir.getReturnValue().trySetValue(Fluidlogged.PROPERTY_FLUID, 0));
    }

}