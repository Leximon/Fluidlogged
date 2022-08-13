package de.leximon.fluidlogged.mixin;

import de.leximon.fluidlogged.FluidloggedMod;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LiquidBlock.class)
public class LiquidBlockMixin {

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V", shift = At.Shift.AFTER))
    private void inject(FlowingFluid flowingFluid, BlockBehaviour.Properties properties, CallbackInfo ci) {
        FluidloggedMod.fluidBlocks.put(flowingFluid, (LiquidBlock) (Object) this);
    }

}
