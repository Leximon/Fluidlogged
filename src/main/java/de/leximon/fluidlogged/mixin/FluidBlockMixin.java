package de.leximon.fluidlogged.mixin;

import de.leximon.fluidlogged.Fluidlogged;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FluidBlock.class)
public class FluidBlockMixin {

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V", shift = At.Shift.AFTER))
    private void getFluidBlocks(FlowableFluid fluid, AbstractBlock.Settings settings, CallbackInfo ci) {
        Fluidlogged.fluidBlocks.put(fluid, (FluidBlock) (Object) this);
    }

}
