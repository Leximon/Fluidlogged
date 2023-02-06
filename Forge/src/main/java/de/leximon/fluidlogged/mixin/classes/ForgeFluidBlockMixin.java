package de.leximon.fluidlogged.mixin.classes;

import de.leximon.fluidlogged.FluidloggedCommon;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LiquidBlock.class)
public class ForgeFluidBlockMixin {

    @Inject(method = "<init>(Lnet/minecraft/world/level/material/FlowingFluid;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V", at = @At(value = "RETURN"))
    private void getFluidBlocks(FlowingFluid fluid, BlockBehaviour.Properties settings, CallbackInfo ci) {
        FluidloggedCommon.fluidBlocks.put(fluid, (LiquidBlock) (Object) this);
    }
}
