package de.leximon.fluidlogged.mixin;

import de.leximon.fluidlogged.Fluidlogged;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LiquidBlock.class)
public class FluidBlockMixin
{
    @Inject(method = "<init>(Lnet/minecraft/world/level/material/FlowingFluid;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V",
            at = @At(value = "TAIL"))
    private void inject(FlowingFluid fluid, BlockBehaviour.Properties settings, CallbackInfo ci) {
        Fluidlogged.fluidBlocks.put(fluid, (LiquidBlock) (Object) this);
    }
}
